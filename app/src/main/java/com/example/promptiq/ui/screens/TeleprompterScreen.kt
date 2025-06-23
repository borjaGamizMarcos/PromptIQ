package com.example.promptiq.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptiq.R
import com.example.promptiq.data.local.Guion
import com.example.promptiq.ui.theme.roboto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeleprompterScreen(
    guiones: List<Guion>,
    guionSeleccionado: Guion?,
    onGuionSeleccionar: (Guion) -> Unit,
    fuente: Float,
    colorFondo: String,
    ritmoLectura: Float,
    onVolver: () -> Unit
) {
    var estaLeyendo by remember { mutableStateOf(false) }
    val palabras = remember(guionSeleccionado) {
        guionSeleccionado?.contenido?.split(" ") ?: emptyList()
    }
    var palabraActualIndex by remember { mutableStateOf(0) }

    val scrollState = rememberScrollState()

    // Scroll automático (solo si estaLeyendo)
    LaunchedEffect(estaLeyendo) {
        if (estaLeyendo) {
            while (scrollState.canScrollForward) {
                scrollState.animateScrollBy(ritmoLectura * 10)
                kotlinx.coroutines.delay(100)
            }
        }
    }

    // Fondo adaptado
    val fondoColor = when (colorFondo) {
        "Claro" -> Color.White
        "Oscuro" -> Color(0xFF0A192F)
        "Azul" -> Color(0xFF1E2A78)
        else -> Color(0xFF0A192F)
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
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(scrollState)
                    .background(Color.Transparent)
            ) {
                Text(
                    text = guionSeleccionado?.contenido ?: "Selecciona un guion para comenzar la lectura.",
                    fontSize = fuente.sp,
                    color = if (colorFondo == "Claro") Color.Black else Color(0xFFDFDCCC),
                    fontFamily = roboto
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            val coroutineScope = rememberCoroutineScope()

            // Controles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    coroutineScope.launch {
                    scrollState.scrollTo(0)
                }}) {
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
