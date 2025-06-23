package com.example.promptiq.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptiq.R
import com.example.promptiq.ui.theme.roboto

@Composable
fun AjustesScreen(
    onVolver: () -> Unit,
    onCambiarContraseña: () -> Unit,
    fuente: Float,
    onFuenteChange: (Float) -> Unit,
    colorFondo: String,
    onColorFondoChange: (String) -> Unit,
    ritmoVariable: Boolean,
    onRitmoVariableChange: (Boolean) -> Unit,
    ritmoLectura: Float,
    onRitmoLecturaChange: (Float) -> Unit
) {
    val coloresDisponibles = listOf("Oscuro", "Claro", "Azul")
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Cabecera con flecha y logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                IconButton(onClick = { onVolver() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFFDFDCCC))
                }

                Image(
                    painter = painterResource(id = R.drawable.logo_hor),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                )
            }

            // Título
            Text(
                text = "Ajustes",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = roboto,
                color = Color(0xFFDFDCCC),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tamaño de fuente
            Text("Tamaño de fuente", color = Color(0xFFDFDCCC), fontFamily = roboto)
            Slider(
                value = fuente,
                onValueChange = onFuenteChange,
                valueRange = 12f..40f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Color de fondo
            Text("Color de fondo", color = Color(0xFFDFDCCC), fontFamily = roboto)
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(colorFondo, color = Color(0xFFDFDCCC), fontFamily = roboto)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    coloresDisponibles.forEach { color ->
                        DropdownMenuItem(
                            text = { Text(color) },
                            onClick = {
                                onColorFondoChange(color)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ritmo de lectura variable
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ritmo de lectura variable", color = Color(0xFFDFDCCC), fontFamily = roboto, modifier = Modifier.weight(1f))
                Switch(
                    checked = ritmoVariable,
                    onCheckedChange = onRitmoVariableChange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Ritmo de lectura", color = Color(0xFFDFDCCC), fontFamily = roboto)

            Text(
                text = "Valor actual: %.1fx".format(ritmoLectura),
                color = Color(0xFFDFDCCC),
                fontFamily = roboto,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Slider(
                value = ritmoLectura,
                onValueChange = onRitmoLecturaChange,
                valueRange = 0.5f..3f,
                steps = 4,  // Divide en 5 pasos
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Lento", color = Color(0xFFDFDCCC), fontSize = 12.sp)
                Text("Normal", color = Color(0xFFDFDCCC), fontSize = 12.sp)
                Text("Rápido", color = Color(0xFFDFDCCC), fontSize = 12.sp)
            }


            Spacer(modifier = Modifier.height(32.dp))

            // Botón cambiar contraseña
            Button(
                onClick = onCambiarContraseña,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A5A91))
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Contraseña", tint = Color(0xFFDFDCCC))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cambiar Contraseña", fontFamily = roboto, fontSize = 16.sp, color = Color(0xFFDFDCCC))
            }
        }
    }
}
