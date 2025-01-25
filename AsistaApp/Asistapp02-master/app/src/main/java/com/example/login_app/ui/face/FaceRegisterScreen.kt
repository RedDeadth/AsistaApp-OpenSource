package com.example.login_app.ui.face

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FaceRegisterScreen(viewModel: FaceCaptureViewModel = viewModel()) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var captureState by remember { mutableStateOf("Esperando para capturar imagen") }
    var fileName by remember { mutableStateOf("") }
    var isCapturing by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current // Get lifecycle owner at composition
    val coroutineScope = rememberCoroutineScope()

    when (uiState) {
        is FaceCaptureState.Success -> {
            captureState = "Foto registrada exitosamente"
            imageBitmap = (uiState as FaceCaptureState.Success).image
            fileName = (uiState as FaceCaptureState.Success).fileName
        }
        is FaceCaptureState.Error -> {
            captureState = "Error al capturar imagen: ${(uiState as FaceCaptureState.Error).message}"
        }
        else -> {
            captureState = "Esperando para capturar imagen"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = captureState)

        Spacer(modifier = Modifier.height(16.dp))

        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        val previewView = remember { PreviewView(context) }
        var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

        // Camera setup
        LaunchedEffect(Unit) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()

            preview.setSurfaceProvider(previewView.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            runCatching {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }.onFailure { exc ->
                Log.e("Camera", "Use case binding failed", exc)
            }
        }

        AndroidView(
            factory = { previewView },
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (!isCapturing) {
                isCapturing = true
                captureState = "Capturando imagen en 3 segundos..."
                coroutineScope.launch {
                    delay(3000)
                    imageCapture?.let { capture ->
                        val executor = ContextCompat.getMainExecutor(context)
                        capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val bitmap = imageProxyToBitmap(image)
                                viewModel.captureImage(bitmap, context)
                                image.close()
                                isCapturing = false
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("Camera", "Image capture failed", exception)
                                captureState = "Error al capturar imagen: ${exception.message}"
                                isCapturing = false
                            }
                        })
                    }
                }
            }
        }) {
            Text(text = "Capturar Imagen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        imageBitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "Imagen Capturada")
        }

        if (fileName.isNotEmpty()) {
            Text(text = "Imagen almacenada como: $fileName")
        }
    }
}
private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    // Obtener la rotación de la imagen
    val rotation = image.imageInfo.rotationDegrees

    // Decodificar el array de bytes en un bitmap
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    // Crear una matriz para rotar la imagen si es necesario
    val matrix = Matrix()
    matrix.postRotate(rotation.toFloat())

    // Rotar el bitmap si es necesario y retornarlo
    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}