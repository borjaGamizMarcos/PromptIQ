package com.example.promptiq.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AdaptativeScrollTestScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val fullText = remember {
        """Bienvenido al teleprompter adaptativo. Este texto ha sido diseñado para comprobar cómo se comporta el sistema con párrafos más extensos. A medida que lees este contenido en voz alta, el sistema irá reconociendo tus palabras y ajustando tanto el scroll como la velocidad del texto. Este mecanismo permite que la experiencia de lectura sea más natural, especialmente en contextos de presentaciones, vídeos o discursos. Continúa leyendo en voz alta hasta que el sistema se sincronice completamente con tu ritmo de dicción."""
            .split(" ")
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

    val displayedText = buildAnnotatedString {
        fullText.forEachIndexed { index, word ->
            when {
                index < scrollIndex -> withStyle(style = SpanStyle(color = Color.Gray)) { append("$word ") }
                index == scrollIndex -> withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) { append("$word ") }
                else -> append("$word ")
            }
        }
    }

    suspend fun adaptiveScroll() {
        var lastRecalcTime = System.currentTimeMillis()
        var lastRecognizedIndex = 0

        while (scrollIndex < fullText.size) {
            scrollIndex++
            scrollState.animateScrollTo(scrollIndex * 30)
            delay((60000 / wpm).toLong())

            val now = System.currentTimeMillis()
            if (now - lastRecalcTime >= 15000) {
                val newRecognizedWords = recognizedWords.drop(lastRecognizedIndex)
                if (newRecognizedWords.size >= 5) {
                    val elapsedSeconds = (now - lastRecalcTime) / 1000f
                    val newWps = newRecognizedWords.size / elapsedSeconds
                    val newWpm = (newWps * 60).roundToInt().coerceIn(80, 1000)
                    if (newWpm != wpm) {
                        wpm = newWpm
                        val lastWordSpoken = newRecognizedWords.last()
                        lastRecognizedWord = lastWordSpoken
                        val matchIndex = fullText.indexOfLast { it.equals(lastWordSpoken, ignoreCase = true) }
                        if (matchIndex != -1 && matchIndex > scrollIndex) {
                            scrollIndex = matchIndex + 1
                            scrollState.animateScrollTo(scrollIndex * 30)
                        }
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
                    recognizedWords = recognizedWords + it.joinToString(" ").split(" ")
                }
                recognizer?.startListening(intent)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                    recognizedWords = recognizedWords + it.joinToString(" ").split(" ")
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Velocidad estimada: $wpm wpm", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
        Text("Última palabra reconocida: $lastRecognizedWord", fontSize = 14.sp, color = Color.DarkGray)

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Text(
                text = displayedText,
                fontSize = 20.sp,
                textAlign = TextAlign.Justify
            )
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
                            scrollState.animateScrollTo(scrollIndex * 30)
                        }
                    }

                    isCalibrating = false
                    isCalibrated = true

                    val elapsedSeconds = 5f
                    val wordsSpoken = recognizedWords.size
                    val wps = if (wordsSpoken > 0) wordsSpoken / elapsedSeconds else 2f
                    wpm = (wps * 60).roundToInt().coerceIn(80, 1000)

                    launch { adaptiveScroll() }
                }
            }
        }) {
            Text(if (!isListening) "Iniciar Lectura" else if (isCalibrating) "Calibrando..." else "Reconociendo...")
        }
    }

    LaunchedEffect(Unit) {
        ActivityCompat.requestPermissions(
            context as android.app.Activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            123
        )
    }
}
