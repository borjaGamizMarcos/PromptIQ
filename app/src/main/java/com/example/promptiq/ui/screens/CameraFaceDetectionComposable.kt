package com.example.promptiq.ui.screens

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraFaceDetectionComposable() {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    if (cameraPermissionState.status.isGranted) {
        var mirandoPantalla by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreviewWithFaceDetection(context) { mirando ->
                mirandoPantalla = mirando
            }

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(100.dp)
                    .background(if (mirandoPantalla) Color.Green else Color.Red)
                    .align(Alignment.TopCenter)
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Se requiere permiso de cámara")
        }
    }
}

@Composable
fun CameraPreviewWithFaceDetection(context: Context, onMiradaDetectada: (Boolean) -> Unit) {
    val lifecycleOwner = LocalContext.current as LifecycleOwner
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var isProcessing by remember { mutableStateOf(false) }

    val detector: FaceDetector = remember {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .enableTracking()
            .build()
        FaceDetection.getClient(options)
    }

    AndroidView(
        factory = { ctx ->
            val previewView = androidx.camera.view.PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    if (isProcessing) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    isProcessing = true
                    processImage(imageProxy, detector) { mirando ->
                        isProcessing = false
                        onMiradaDetectada(mirando)
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Error al enlazar cámara", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )
}

fun processImage(
    imageProxy: ImageProxy,
    detector: FaceDetector,
    onResult: (Boolean) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        onResult(false)
        return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    detector.process(image)
        .addOnSuccessListener { faces ->
            val mirando = faces.any { isLookingAtScreen(it) }
            onResult(mirando)
        }
        .addOnFailureListener { e ->
            Log.e("MLKit", "Error analizando imagen", e)
            onResult(false)
        }
        .addOnCompleteListener {
            try {
                imageProxy.close()
            } catch (e: Exception) {
                Log.e("MLKit", "Error al cerrar imageProxy", e)
            }
        }
}

fun isLookingAtScreen(face: Face): Boolean {
    val eulerY = face.headEulerAngleY  // Izquierda (-) a Derecha (+)
    val eulerX = face.headEulerAngleX  // Abajo (-) a Arriba (+)

    val mirandoHaciaPantalla = eulerY in -15f..15f && eulerX in -15f..15f

    Log.d("MLKit", "EulerX=$eulerX, EulerY=$eulerY -> Mirando pantalla: $mirandoHaciaPantalla")
    return mirandoHaciaPantalla
}
