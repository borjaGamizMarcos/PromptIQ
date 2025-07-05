
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.promptiq.R
import com.example.promptiq.data.local.Guion
import com.example.promptiq.ui.theme.roboto
import com.google.accompanist.flowlayout.FlowRow
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.math.roundToInt

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeleprompterInteligenteScreen(
    guiones: List<Guion>,
    guionSeleccionado: Guion?,
    onGuionSeleccionar: (Guion) -> Unit,
    fuente: Float,
    colorFondo: String,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val palabras = remember(guionSeleccionado) {
        guionSeleccionado?.contenido
            ?.replace(Regex("\\s+"), " ")
            ?.trim()
            ?.split(" ") ?: emptyList()
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
    var mostrarPopupAjustes by remember { mutableStateOf(false) }
    var currentFuente by remember { mutableStateOf(fuente) }
    var shouldScroll by remember { mutableStateOf(false) }
    var wasPaused by remember { mutableStateOf(false) }





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

        while (scrollIndex < palabras.size && shouldScroll) {
            if (mirandoPantalla) {
                val correctionFactor = 0.65f  // puedes ajustarlo a tu gusto (ej: 0.6–0.8)
                val tiempoPorPalabra = ((60000 / wpm) * correctionFactor).toLong()

                Log.d("SCROLL", "scrollIndex=$scrollIndex, palabras.size=${palabras.size}, wpm=$wpm")


                val targetY = posicionesY[scrollIndex] ?: 0
                val maxScrollY = scrollState.maxValue

// Si ya estamos muy cerca del final y el targetY no provoca scroll, forzamos uno artificial
                val effectiveTarget = if (targetY >= maxScrollY - 10) {
                    (maxScrollY + 200).coerceAtMost(scrollState.maxValue + 300)
                } else {
                    targetY
                }
                scrollState.animateScrollTo(effectiveTarget)


                // Desplazamiento más rápido si se ha interrumpido recientemente
                delay((tiempoPorPalabra * 0.7).toLong())
                scrollIndex++
            } else {
                delay(200)
                continue
            }

            if (scrollIndex >= palabras.lastIndex - 3) {
                // Forzamos ritmo final fluido aunque no haya más input de voz
                val finalDelay = ((60000 / wpm) * 0.75f).toLong().coerceAtLeast(100L)
                posicionesY[scrollIndex]?.let {
                    scrollState.animateScrollTo(it)
                }
                delay(finalDelay)
                scrollIndex++
                continue
            }


            val now = System.currentTimeMillis()
            if (now - lastRecalcTime >= 15000) {
                val newRecognizedWords = recognizedWords.drop(lastRecognizedIndex)
                if (newRecognizedWords.size >= 5) {
                    val elapsedSeconds = (now - lastRecalcTime) / 1000f
                    val newWps = newRecognizedWords.size / elapsedSeconds
                    val newWpm = (newWps * 60).roundToInt().coerceIn(100, 900)
                    wpm = (0.7f * wpm + 0.3f * newWpm).roundToInt()

                    val lastWordSpoken = newRecognizedWords.last()
                    lastRecognizedWord = lastWordSpoken
                    val safeRange = scrollIndex..(scrollIndex + 2).coerceAtMost(palabras.lastIndex)
                    val matchIndex = safeRange.firstOrNull {
                        palabras[it].equals(lastWordSpoken, ignoreCase = true)
                    }

                    if (matchIndex != null && matchIndex == scrollIndex) {
                        scrollIndex++
                    }

                }else {
                    Log.d("WPM", "No hay suficientes nuevas palabras para recalcular. Tamaño=${newRecognizedWords.size}")
                }
                lastRecognizedIndex = recognizedWords.size
                lastRecalcTime = now
            }
        }
    }

    fun startContinuousRecognition() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        audioManager.adjustStreamVolume(android.media.AudioManager.STREAM_SYSTEM, android.media.AudioManager.ADJUST_MUTE, 0)

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

    val fondoColor = when (colorFondo) {
        "Claro" -> Color.White
        "Oscuro" -> Color(0xFF0A192F)
        "Azul" -> Color(0xFF1E2A78)
        else -> Color(0xFF0A192F)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
            .padding(16.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            val color= if (colorFondo == "Claro") Color(0xFF0A192F) else Color.White
            IconButton(onClick = { recognizer?.stopListening()
                recognizer?.cancel()
                recognizer?.destroy()
                recognizer = null
                isListening = false
                shouldScroll = false
                onVolver() },
                modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = color)
            }
            Image(
                painter = painterResource(id = R.drawable.logo_hor),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.Center)
            )
            IconButton(
                onClick = { mostrarPopupAjustes = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = color)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Velocidad estimada: $wpm wpm", fontSize = 16.sp, color= if (colorFondo == "Claro") Color(0xFF0A192F) else Color.White)
        Text("Última palabra reconocida: $lastRecognizedWord", fontSize = 14.sp, color = if (colorFondo == "Claro") Color(0xFF0A192F) else Color.LightGray)
        Text("Mirando a la pantalla: ${if (mirandoPantalla) "Sí" else "No"}", fontSize = 14.sp, color = if (mirandoPantalla) Color.Green else Color.Red)

        Spacer(modifier = Modifier.height(8.dp))

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                readOnly = true,
                value = guionSeleccionado?.titulo ?: "Seleccionar guion",
                onValueChange = {},
                label = { Text("Guion", color= if (colorFondo == "Claro") Color(0xFF0A192F) else Color.White ) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                textStyle = TextStyle(color= if (colorFondo == "Claro") Color(0xFF0A192F) else Color.White)

            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                guiones.forEach { guion ->
                    DropdownMenuItem(
                        text = { Text(guion.titulo)  },
                        onClick = {
                            onGuionSeleccionar(guion)
                            scrollIndex = 0
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(8.dp)
        ) {
            FlowRow(
                mainAxisSpacing = 4.dp,
                crossAxisSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                palabras.forEachIndexed { index, palabra ->
                    val color = when {
                        index < scrollIndex -> Color.Gray.copy(alpha = 0.7f)
                        index == scrollIndex -> Color.Yellow
                        else -> if (colorFondo == "Claro") Color(0xFF0A192F) else Color(0xFFDFDCCC)
                    }

                    Text(
                        text = palabra,
                        fontSize = currentFuente.sp,
                        fontFamily = roboto,
                        fontWeight = if (index == scrollIndex) FontWeight.Bold else FontWeight.Normal,
                        color = color,
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .onGloballyPositioned { coords ->
                                posicionesY[index] = coords.positionInParent().y.toInt()
                            }
                    )
                }
                Spacer(modifier = Modifier.height(400.dp))

            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val color= if (colorFondo == "Claro") Color(0xFF0A192F) else Color.White
            IconButton(onClick = {
                coroutineScope.launch { scrollState.scrollTo(0) }
                scrollIndex = 0
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Reiniciar", tint = color)
            }

            IconButton(onClick = {
                if (!isListening) {
                    isListening = true
                    shouldScroll = true

                    if (!wasPaused) {
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
                                if (scrollIndex < palabras.size) {

                                    posicionesY[scrollIndex]?.let { scrollState.animateScrollTo(it) }
                                    scrollIndex++
                                }
                            }

                            isCalibrating = false
                            isCalibrated = true

                            val elapsedSeconds = 5f
                            val wordsSpoken = recognizedWords.size
                            val wps = if (wordsSpoken > 0) wordsSpoken / elapsedSeconds else 2f
                            wpm = (wps * 60).roundToInt().coerceIn(150, 900)

                            launch { adaptiveScroll() }
                        }
                    } else {
                        wasPaused = false
                        startContinuousRecognition()
                        coroutineScope.launch { adaptiveScroll() }
                    }
                }
            }) {
                Icon(
                    if (!isListening) Icons.Default.PlayArrow else Icons.Default.Mic,
                    contentDescription = "Iniciar",
                    tint = color
                )
            }

            IconButton(onClick = {
                isListening = false
                shouldScroll = false
                recognizer?.stopListening()
                recognizer?.cancel()
                recognizer?.destroy()
                recognizer = null
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                audioManager.adjustStreamVolume(android.media.AudioManager.STREAM_SYSTEM, android.media.AudioManager.ADJUST_UNMUTE, 0)

                wasPaused=true

            }) {
                Icon(Icons.Default.Stop, contentDescription = "Detener", tint = color)
            }

            IconButton(onClick = {
                recognizer?.stopListening()
                recognizer?.cancel()
                recognizer?.destroy()
                recognizer = null
                isListening = false
                shouldScroll = false
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                audioManager.adjustStreamVolume(android.media.AudioManager.STREAM_SYSTEM, android.media.AudioManager.ADJUST_UNMUTE, 0)

                onVolver()
            }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint =color)
            }

        }
        if (mostrarPopupAjustes) {
            AlertDialog(
                onDismissRequest = { mostrarPopupAjustes = false },
                confirmButton = {
                    TextButton(onClick = { mostrarPopupAjustes = false }) {
                        Text("Aceptar", color= if (colorFondo == "Claro") Color(0xFF0A192F) else Color.White)
                    }
                },
                containerColor = fondoColor,

                title = {
                    Text("Tamaño de fuente", color= if (colorFondo == "Claro") Color(0xFF0A192F) else Color.White, fontFamily = roboto)
                },
                text = {
                    Column {
                        Slider(
                            value = currentFuente,
                            onValueChange = { nuevaFuente ->
                                currentFuente = nuevaFuente
                            },
                            valueRange = 12f..40f
                        )
                    }
                }
            )
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
        factory = { ctx: Context ->
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
            .height(1.dp)
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
