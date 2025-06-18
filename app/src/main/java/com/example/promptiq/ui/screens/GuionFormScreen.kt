package com.example.promptiq.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.promptiq.data.local.Guion
import com.example.promptiq.ui.theme.roboto

@Composable
fun GuionFormScreen(
    guion: Guion?,
    onGuardar: (Guion) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf(guion?.titulo ?: "") }
    var contenido by remember { mutableStateOf(guion?.contenido ?: "") }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A192F))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flecha y Logo
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
            ) {
                IconButton(onClick = { onCancelar() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFFDFDCCC)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.logo_hor),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                )
            }

            // Título del formulario
            Text(
                text = if (guion == null) "Nuevo Guion" else "Editar Guion",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = roboto,
                color = Color(0xFFDFDCCC)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo título
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título", color = Color(0xFFDFDCCC)) },
                textStyle = LocalTextStyle.current.copy(color = Color(0xFFDFDCCC)),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFDFDCCC),
                    focusedBorderColor = Color(0xFF3A5A91),
                    cursorColor = Color(0xFFDFDCCC),
                    unfocusedLabelColor = Color(0xFFDFDCCC)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo contenido
            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Contenido", color = Color(0xFFDFDCCC)) },
                textStyle = LocalTextStyle.current.copy(color = Color(0xFFDFDCCC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFDFDCCC),
                    focusedBorderColor = Color(0xFF3A5A91),
                    cursorColor = Color(0xFFDFDCCC),
                    unfocusedLabelColor = Color(0xFFDFDCCC)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onCancelar,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text("Cancelar", color = Color(0xFFDFDCCC))
                }

                Button(
                    onClick = {
                        val guionFinal = guion?.copy(titulo = titulo, contenido = contenido)
                            ?: Guion(titulo = titulo, contenido = contenido, usuarioEmail = "")
                        onGuardar(guionFinal)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A5A91))
                ) {
                    Text("Guardar", color = Color(0xFFDFDCCC))
                }
            }
        }
    }
}
