package com.example.promptiq.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.promptiq.ui.theme.roboto
import com.google.accompanist.flowlayout.FlowRow
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.math.roundToInt

@Composable
fun AdaptativeScrollTestScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val fullText = remember {
        ("""Bienvenido al teleprompter adaptativo. Este texto ha sido diseñado para comprobar cómo se comporta el sistema con párrafos más extensos. A medida que lees este contenido en voz alta, el sistema irá reconociendo tus palabras y ajustando tanto el scroll como la velocidad del texto. Este mecanismo permite que la experiencia de lectura sea más natural, especialmente en contextos de presentaciones, vídeos o discursos. Continúa leyendo en voz alta hasta que el sistema se sincronice completamente con tu ritmo de dicción."""
            .repeat(10)).split(" ")
    }

    var recognizedWords by remember { mutableStateOf(listOf<String>()) }
    var isListening by remember { mutableStateOf(false) }
    var isCalibrating by remember { mutableStateOf(false) }
    var wpm by remember { mutableStateOf(150) }
    var scrollIndex by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    var isCalibrated by remember { mutableStateOf(false) }
    var recognizer: SpeechRecognizer? = remember { null }
    var lastRecognizedWord by remember { mutableStateOf("") }
    val posicionesY = remember { mutableStateMapOf<Int, Int>() }

    var mirandoPantalla by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA),
            123
        )
    }

    CameraPreviewWithFaceDetection(context) { mirandoPantalla = it }

    suspend fun adaptiveScroll() {
        var lastRecalcTime = System.currentTimeMillis()
        var lastRecognizedIndex = 0

        while (scrollIndex < fullText.size) {
            if (mirandoPantalla) {
                posicionesY[scrollIndex]?.let { scrollState.animateScrollTo(it) }
                delay((60000 / wpm).toLong())
                scrollIndex++
            } else {
                delay(200)
                continue
            }

            if (scrollIndex >= fullText.lastIndex) {
                recognizer?.stopListening()
                recognizer?.cancel()
                recognizer?.destroy()
                recognizer = null
                isListening = false
                break
            }

            val now = System.currentTimeMillis()
            if (now - lastRecalcTime >= 15000) {
                val newRecognizedWords = recognizedWords.drop(lastRecognizedIndex)
                if (newRecognizedWords.size >= 5) {
                    val elapsedSeconds = (now - lastRecalcTime) / 1000f
                    val newWps = newRecognizedWords.size / elapsedSeconds
                    val newWpm = (newWps * 60).roundToInt().coerceIn(80, 300)
                    wpm = (0.7f * wpm + 0.3f * newWpm).roundToInt()

                    val lastWordSpoken = newRecognizedWords.last()
                    lastRecognizedWord = lastWordSpoken
                    val matchIndex = fullText.withIndex()
                        .filter { it.value.equals(lastWordSpoken, ignoreCase = true) && it.index > scrollIndex }
                        .minByOrNull { it.index }?.index

                    if (matchIndex != null && matchIndex > scrollIndex) {
                        scrollIndex = matchIndex + 1
                    }
                }
                lastRecognizedIndex = recognizedWords.size
                lastRecalcTime = now
            }
        }
    }

    fun startContinuousRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                    val newWords = it.joinToString(" ").split(" ")
                    recognizedWords = recognizedWords + newWords.filterNot { w -> recognizedWords.contains(w) }
                }
                recognizer?.startListening(intent)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                    val newWords = it.joinToString(" ").split(" ")
                    recognizedWords = recognizedWords + newWords.filterNot { w -> recognizedWords.contains(w) }
                }
            }

            override fun onBeginningOfSpeech() {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(p0: Int) { recognizer?.startListening(intent) }
            override fun onEvent(p0: Int, p1: Bundle?) {}
            override fun onReadyForSpeech(p0: Bundle?) {}
            override fun onRmsChanged(p0: Float) {}
        })

        recognizer?.startListening(intent)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A192F)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Velocidad estimada: $wpm wpm", fontSize = 18.sp, color = Color.White)
        Text("Última palabra reconocida: $lastRecognizedWord", fontSize = 14.sp, color = Color.LightGray)
        Text("Mirando a la pantalla: ${if (mirandoPantalla) "Sí" else "No"}", fontSize = 14.sp, color = if (mirandoPantalla) Color.Green else Color.Red)

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(8.dp)
        ) {
            FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                fullText.forEachIndexed { index, palabra ->
                    val color = when {
                        index < scrollIndex -> Color.Gray.copy(alpha = 0.7f)
                        index == scrollIndex -> Color.Yellow
                        else -> Color(0xFFDFDCCC)
                    }

                    Text(
                        text = "$palabra ",
                        fontSize = 20.sp,
                        fontFamily = roboto,
                        fontWeight = if (index == scrollIndex) FontWeight.Bold else FontWeight.Normal,
                        color = color,
                        modifier = Modifier
                            .padding(end = 4.dp, bottom = 4.dp)
                            .onGloballyPositioned { coords ->
                                posicionesY[index] = coords.positionInParent().y.toInt()
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (!isListening) {
                isListening = true
                isCalibrating = true
                recognizedWords = listOf()
                scrollIndex = 0
                isCalibrated = false

                recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                startContinuousRecognition()

                coroutineScope.launch {
                    val initialDelay = (60000 / wpm).toLong()
                    val startTime = System.currentTimeMillis()

                    while (System.currentTimeMillis() - startTime < 5000) {
                        delay(initialDelay)
                        if (scrollIndex < fullText.size) {
                            scrollIndex++
                            posicionesY[scrollIndex]?.let { scrollState.animateScrollTo(it) }
                        }
                    }

                    isCalibrating = false
                    isCalibrated = true

                    val elapsedSeconds = 5f
                    val wordsSpoken = recognizedWords.size
                    val wps = if (wordsSpoken > 0) wordsSpoken / elapsedSeconds else 2f
                    wpm = (wps * 60).roundToInt().coerceIn(80, 300)

                    launch { adaptiveScroll() }
                }
            }
        }) {
            Text(if (!isListening) "Iniciar Lectura" else if (isCalibrating) "Calibrando..." else "Reconociendo...")
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
            .height(1.dp) // Oculto visualmente, pero activo
    )
}

@OptIn(ExperimentalGetImage::class)
fun processImage(
    imageProxy: ImageProxy,
    detector: FaceDetector,
    onResult: (Boolean) -> Unit
) {
    val mediaImage = imageProxy.image ?: return imageProxy.close().also { onResult(false) }
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    detector.process(image)
        .addOnSuccessListener { faces ->
            val mirando = faces.any { isLookingAtScreen(it) }
            onResult(mirando)
        }
        .addOnFailureListener {
            onResult(false)
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

fun isLookingAtScreen(face: Face): Boolean {
    val eulerY = face.headEulerAngleY
    val eulerX = face.headEulerAngleX
    return eulerY in -15f..15f && eulerX in -15f..15f
}
