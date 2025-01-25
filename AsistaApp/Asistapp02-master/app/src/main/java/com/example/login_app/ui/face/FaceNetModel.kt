package com.example.login_app.ui.face

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.sqrt

class FaceNetModel private constructor(context: Context) {
    private val interpreter: Interpreter

    init {
        val options = Interpreter.Options().apply {
            setNumThreads(4)
        }
        interpreter = Interpreter(loadModelFile(context, "mobile_face_net.tflite"), options)
    }

    fun compareImages(image1: Bitmap, image2: Bitmap): Float {
        // Crear un array de entrada para ambas imágenes [2, 112, 112, 3]
        val inputArray = Array(2) { Array(112) { Array(112) { FloatArray(3) } } }

        // Procesar ambas imágenes
        preprocessImage(image1, inputArray[0])
        preprocessImage(image2, inputArray[1])

        // Crear array de salida [2, 192]
        val outputArray = Array(2) { FloatArray(192) }

        // Ejecutar la inferencia
        interpreter.run(inputArray, outputArray)

        // Calcular similaridad entre los embeddings
        return calculateCosineSimilarity(outputArray[0], outputArray[1])
    }

    private fun preprocessImage(bitmap: Bitmap, output: Array<Array<FloatArray>>) {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 112, 112, true)

        for (y in 0 until 112) {
            for (x in 0 until 112) {
                val pixel = resizedBitmap.getPixel(x, y)
                // Normalizar a [-1, 1] y almacenar en formato RGB
                output[y][x][0] = ((pixel shr 16 and 0xFF) - 127.5f) / 127.5f // R
                output[y][x][1] = ((pixel shr 8 and 0xFF) - 127.5f) / 127.5f  // G
                output[y][x][2] = ((pixel and 0xFF) - 127.5f) / 127.5f        // B
            }
        }
    }

    private fun calculateCosineSimilarity(embedding1: FloatArray, embedding2: FloatArray): Float {
        var dotProduct = 0f
        var norm1 = 0f
        var norm2 = 0f

        for (i in embedding1.indices) {
            dotProduct += embedding1[i] * embedding2[i]
            norm1 += embedding1[i] * embedding1[i]
            norm2 += embedding2[i] * embedding2[i]
        }

        norm1 = sqrt(norm1)
        norm2 = sqrt(norm2)

        return dotProduct / (norm1 * norm2)
    }

    companion object {
        const val SIMILARITY_THRESHOLD = 0.7f

        fun create(context: Context): FaceNetModel {
            return FaceNetModel(context)
        }

        private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
            val fileDescriptor = context.assets.openFd(modelName)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }
}