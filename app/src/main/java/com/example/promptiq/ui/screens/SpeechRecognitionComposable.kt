package com.example.promptiq.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SpeechRecognitionComposable() {
    val context = LocalContext.current
    val audioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) {
        audioPermissionState.launchPermissionRequest()
    }

    if (audioPermissionState.status.isGranted) {
        SpeechRecognitionUI(context)
    } else {
        Text("Se requiere permiso de micrófono")
    }
}

@Composable
fun SpeechRecognitionUI(context: Context) {
    var recognizedText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var wordCount by remember { mutableStateOf(0) }
    var speechRate by remember { mutableStateOf(0.0) }
    var startTime by remember { mutableStateOf(0L) }

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {
                    startTime = System.currentTimeMillis()
                }

                override fun onResults(results: Bundle?) {
                    isListening = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull().orEmpty()
                    recognizedText = text
                    wordCount = text.trim().split("\\s+".toRegex()).size
                    val durationSeconds = (System.currentTimeMillis() - startTime) / 1000.0
                    speechRate = if (durationSeconds > 0) wordCount / durationSeconds else 0.0
                }

                override fun onError(error: Int) {
                    isListening = false
                    Log.e("SpeechRecognizer", "Error: $error")
                }

                override fun onEndOfSpeech() {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
                override fun onRmsChanged(rmsdB: Float) {}
            })
        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        }
        speechRecognizer.startListening(intent)
        isListening = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { startListening() },
            enabled = !isListening
        ) {
            Text(if (isListening) "Escuchando..." else "Iniciar reconocimiento")
        }

        Text("Texto reconocido: $recognizedText")
        Text("Palabras detectadas: $wordCount")
        Text("Velocidad estimada: %.2f palabras/seg".format(speechRate))
    }
}
