package com.example.promptiq.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun AdaptativeScrollTestScreen() {
    val context = LocalContext.current
    val fullText = remember {
        """
            Este es un texto de prueba para el teleprompter adaptativo. Puedes comenzar a leer en voz alta mientras se calibra la velocidad de tu dicción.
            El sistema detectará automáticamente cuántas palabras estás diciendo por segundo y ajustará el ritmo del scroll de forma dinámica.
            Este proceso se repetirá cada 15 segundos para adaptarse a tu velocidad de lectura.
            Puedes seguir leyendo este texto con normalidad, observando cómo el scroll avanza y cómo se actualizan las métricas.
            Esta es una prueba diseñada para validar que el scroll acompaña al ritmo real del lector.
            Continúa leyendo y observa cómo el sistema responde.
        """.trimIndent()
    }

    var detectedWords by remember { mutableStateOf(0) }
    var wps by remember { mutableStateOf(0f) }
    var avgWps by remember { mutableStateOf(0f) }
    var scrollOffset by remember { mutableStateOf(0f) }
    val scrollState = rememberScrollState()
    val handler = remember { Handler(Looper.getMainLooper()) }

    LaunchedEffect(Unit) {
        ActivityCompat.requestPermissions(
            (context as android.app.Activity),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            1
        )

        delay(1000)

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)


        var wordsSum = 0
        var intervals = 0

        val listener = object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val words = matches?.joinToString(" ")?.split(" ")?.filter { it.isNotBlank() } ?: emptyList()
                detectedWords += words.size
                wordsSum += words.size
                intervals++
                wps = words.size / 5f
                avgWps = wordsSum / (intervals * 5f)
                scrollOffset += wps * 60f
            }
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer.setRecognitionListener(listener)

        // Calibración inicial 5 segundos
        speechRecognizer.startListening(intent)
        delay(5000)
        speechRecognizer.stopListening()

        // Scroll y actualizaciones cada 15 s
        while (true) {
            delay(15000)
            speechRecognizer.startListening(intent)
            delay(5000)
            speechRecognizer.stopListening()
        }
    }

    LaunchedEffect(scrollOffset) {
        scrollState.animateScrollTo(scrollOffset.toInt())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Detectadas: $detectedWords", color = Color.White)
            Text("Velocidad: ${"%.2f".format(wps)} wps", color = Color.White)
            Text("Media: ${"%.2f".format(avgWps)} wps", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = fullText,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }
    }
}
