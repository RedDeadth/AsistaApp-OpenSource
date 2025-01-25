package com.example.login_app.ui.face

import android.graphics.Bitmap
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

class FaceCaptureViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<FaceCaptureState>(FaceCaptureState.Initial)
    val uiState: StateFlow<FaceCaptureState> = _uiState

    companion object {
        private const val FACES_DIR = "AssistaApp/faces" // Directorio para fotos registradas
        private const val TEMP_DIR = "AssistaApp/facetemp" // Directorio para fotos temporales
        private const val ATTENDANCE_DIR = "AssistaApp/attendance"
    }
    // Función para guardar imagen de registro
    fun captureImage(bitmap: Bitmap, context: Context) {
        saveCapturedImage(bitmap, context, FACES_DIR)
    }
    // Función para obtener la imagen registrada
    fun getRegisteredFaceImage(context: Context): Bitmap? {
        val dir = File(context.getExternalFilesDir(null), FACES_DIR)
        val files = dir.listFiles()
        return files?.firstOrNull()?.let { file ->
            BitmapFactory.decodeFile(file.absolutePath)
        }
    }

    // Nueva función específica para capturar imagen temporal
    fun captureTempImage(bitmap: Bitmap, context: Context) {
        saveCapturedImage(bitmap, context, TEMP_DIR)
    }
    // Función para eliminar imágenes temporales
    fun deleteTempImage(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val dir = File(context.getExternalFilesDir(null), TEMP_DIR)
            if (dir.exists()) {
                dir.listFiles()?.forEach { it.delete() }
            }
        }
    }
    // Función para registrar la asistencia
    fun registerAttendance(userId: String, similarity: Float, timestamp: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val attendanceRecord = """
                    Usuario: $userId
                    Fecha y Hora: $timestamp
                    Similaridad: ${String.format("%.2f", similarity)}
                    --------------------------------
                """.trimIndent()

                val dir = File(context.getExternalFilesDir(null), ATTENDANCE_DIR)
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val file = File(dir, "attendance_log.txt")
                file.appendText(attendanceRecord + "\n")

                withContext(Dispatchers.Main) {
                    _uiState.value = FaceCaptureState.Success(
                        image = null,
                        fileName = file.absolutePath,
                        currentUser = userId,
                        currentDateTime = timestamp
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = FaceCaptureState.Error("Error al registrar asistencia: ${e.message}")
                }
            }
        }
    }


    private fun saveCapturedImage(bitmap: Bitmap, context: Context, directory: String) {
        viewModelScope.launch {
            val dir = File(context.getExternalFilesDir(null), directory)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val file = File(dir, "captured_face_${System.currentTimeMillis()}.jpg")

            try {
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                }
                _uiState.value = FaceCaptureState.Success(bitmap, file.absolutePath)
                Log.d("ImageCapture", "Imagen guardada como: ${file.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
                _uiState.value = FaceCaptureState.Error("Error al guardar la imagen: ${e.message}")
                Log.e("ImageCapture", "Error al guardar la imagen: ${e.message}")
            }
        }
    }
    fun analyzeModelAndImages(context: Context, bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                Log.d("ModelAnalysis", "=== Comenzando análisis del modelo y las imágenes ===")

                // Análisis de la imagen de entrada
                Log.d("ModelAnalysis", """
                    Imagen Original:
                    - Ancho: ${bitmap.width}
                    - Alto: ${bitmap.height}
                    - Config: ${bitmap.config}
                    - Tamaño en bytes: ${bitmap.byteCount}
                """.trimIndent())

                // Cargar el modelo correctamente
                val modelFile = context.assets.openFd("mobile_face_net.tflite")
                val fileChannel = FileInputStream(modelFile.fileDescriptor).channel
                val startOffset = modelFile.startOffset
                val declaredLength = modelFile.declaredLength
                val mappedByteBuffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY,
                    startOffset,
                    declaredLength
                )

                val interpreter = Interpreter(mappedByteBuffer, Interpreter.Options().apply {
                    setNumThreads(4)
                })

                // Obtener información del modelo
                Log.d("ModelAnalysis", """
                    Información del Modelo:
                    - Número de tensores de entrada: ${interpreter.inputTensorCount}
                    - Número de tensores de salida: ${interpreter.outputTensorCount}
                """.trimIndent())

                // Analizar cada tensor de entrada
                for (i in 0 until interpreter.inputTensorCount) {
                    val inputTensor = interpreter.getInputTensor(i)
                    Log.d("ModelAnalysis", """
                        Tensor de Entrada $i:
                        - Nombre: ${inputTensor.name()}
                        - Shape: ${inputTensor.shape().contentToString()}
                        - Tipo de datos: ${inputTensor.dataType()}
                        - Bytes requeridos: ${inputTensor.numBytes()}
                    """.trimIndent())
                }

                // Analizar cada tensor de salida
                for (i in 0 until interpreter.outputTensorCount) {
                    val outputTensor = interpreter.getOutputTensor(i)
                    Log.d("ModelAnalysis", """
                        Tensor de Salida $i:
                        - Nombre: ${outputTensor.name()}
                        - Shape: ${outputTensor.shape().contentToString()}
                        - Tipo de datos: ${outputTensor.dataType()}
                        - Bytes requeridos: ${outputTensor.numBytes()}
                    """.trimIndent())
                }

                // Probar diferentes configuraciones de preprocesamiento
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 112, 112, true)
                Log.d("ModelAnalysis", """
                    Imagen Redimensionada:
                    - Ancho: ${resizedBitmap.width}
                    - Alto: ${resizedBitmap.height}
                    - Config: ${resizedBitmap.config}
                    - Tamaño en bytes: ${resizedBitmap.byteCount}
                """.trimIndent())

                // Analizar los píxeles de la imagen
                val pixels = IntArray(resizedBitmap.width * resizedBitmap.height)
                resizedBitmap.getPixels(
                    pixels, 0, resizedBitmap.width,
                    0, 0, resizedBitmap.width, resizedBitmap.height
                )

                Log.d("ModelAnalysis", """
                    Análisis de Píxeles:
                    - Número total de píxeles: ${pixels.size}
                    - Rango de valores R: ${pixels.map { it shr 16 and 0xFF }.minOrNull()} a ${pixels.map { it shr 16 and 0xFF }.maxOrNull()}
                    - Rango de valores G: ${pixels.map { it shr 8 and 0xFF }.minOrNull()} a ${pixels.map { it shr 8 and 0xFF }.maxOrNull()}
                    - Rango de valores B: ${pixels.map { it and 0xFF }.minOrNull()} a ${pixels.map { it and 0xFF }.maxOrNull()}
                """.trimIndent())

                // Prueba de dimensiones del tensor
                try {
                    // Crear un tensor de prueba
                    val inputShape = interpreter.getInputTensor(0).shape()
                    val testInput = Array(inputShape[0]) {
                        Array(inputShape[1]) {
                            Array(inputShape[2]) {
                                FloatArray(inputShape[3])
                            }
                        }
                    }

                    Log.d("ModelAnalysis", """
                        Tensor de Prueba Creado:
                        - Dimensiones: ${inputShape.contentToString()}
                        - Tamaño total: ${inputShape.reduce { acc, i -> acc * i }}
                    """.trimIndent())
                } catch (e: Exception) {
                    Log.e("ModelAnalysis", "Error al crear tensor de prueba", e)
                }

                interpreter.close()
                fileChannel.close()

                Log.d("ModelAnalysis", "=== Análisis completado ===")

            } catch (e: Exception) {
                Log.e("ModelAnalysis", "Error durante el análisis", e)
                Log.e("ModelAnalysis", """
                    Detalles del error:
                    - Tipo: ${e.javaClass.simpleName}
                    - Mensaje: ${e.message}
                    - Causa: ${e.cause?.message}
                    
                    Stack Trace:
                    ${e.stackTraceToString()}
                """.trimIndent())
            }
        }
    }


}
