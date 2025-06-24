package com.example.promptiq.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptiq.R
import com.example.promptiq.data.local.Guion
import com.example.promptiq.ui.theme.roboto
import kotlinx.coroutines.launch
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeleprompterScreen(
    guiones: List<Guion>,
    guionSeleccionado: Guion?,
    onGuionSeleccionar: (Guion) -> Unit,
    fuente: Float,
    colorFondo: String,
    ritmoLectura: Float, // palabras por segundo
    onVolver: () -> Unit
) {
    var estaLeyendo by remember { mutableStateOf(false) }
    val palabras = remember(guionSeleccionado) {
        guionSeleccionado?.contenido?.split(" ") ?: emptyList()
    }
    var currentWordIndex by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val posicionesY = remember { mutableStateMapOf<Int, Int>() }
    val density = LocalDensity.current

    // Fondo adaptado
    val fondoColor = when (colorFondo) {
        "Claro" -> Color.White
        "Oscuro" -> Color(0xFF0A192F)
        "Azul" -> Color(0xFF1E2A78)
        else -> Color(0xFF0A192F)
    }

    // Scroll sincronizado a palabra actual
    LaunchedEffect(estaLeyendo) {
        if (estaLeyendo) {
            while (currentWordIndex < palabras.size) {
                currentWordIndex++

                // Calculamos la posición Y acumulada hasta la palabra actual
                posicionesY[currentWordIndex]?.let { targetY ->
                    scrollState.animateScrollTo(targetY)
                }

                val delayPorPalabra = (1000 / ritmoLectura).toLong().coerceAtLeast(100L)
                delay(delayPorPalabra)
            }
            estaLeyendo = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Cabecera
            Box(Modifier.fillMaxWidth()) {
                IconButton(onClick = { onVolver() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFFDFDCCC))
                }
                Image(
                    painter = painterResource(id = R.drawable.logo_hor),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de guión
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    readOnly = true,
                    value = guionSeleccionado?.titulo ?: "Seleccionar guion",
                    onValueChange = {},
                    label = { Text("Guion") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    guiones.forEach { guion ->
                        DropdownMenuItem(
                            text = { Text(guion.titulo) },
                            onClick = {
                                onGuionSeleccionar(guion)
                                currentWordIndex = 0
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Zona de lectura
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(8.dp)
            ) {
                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    palabras.forEachIndexed { index, palabra ->
                        val color = when {
                            index < currentWordIndex -> Color.Gray.copy(alpha = 0.7f)
                            index == currentWordIndex -> Color.Yellow
                            else -> if (colorFondo == "Claro") Color.Black else Color(0xFFDFDCCC)
                        }

                        Text(
                            text = "$palabra ",
                            fontSize = fuente.sp,
                            fontFamily = roboto,
                            color = color,
                            modifier = Modifier
                                .onGloballyPositioned { layoutCoordinates ->
                                    val y = layoutCoordinates.positionInParent().y.toInt()
                                    posicionesY[index] = y
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Controles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        scrollState.scrollTo(0)
                    }
                    currentWordIndex = 0
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reiniciar", tint = Color(0xFFDFDCCC))
                }
                IconButton(onClick = { estaLeyendo = !estaLeyendo }) {
                    Icon(
                        if (estaLeyendo) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Reproducir/Pausar",
                        tint = Color(0xFFDFDCCC)
                    )
                }
                IconButton(onClick = { estaLeyendo = false }) {
                    Icon(Icons.Default.Stop, contentDescription = "Detener", tint = Color(0xFFDFDCCC))
                }
                IconButton(onClick = { onVolver() }) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFFDFDCCC))
                }
            }
        }
    }
}
