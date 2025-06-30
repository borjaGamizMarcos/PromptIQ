package com.example.promptiq.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptiq.R
import com.example.promptiq.ui.theme.roboto

@Composable
fun AyudaScreen(
    onVolver: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Cabecera con icono de volver y logo
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
                text = "Ayuda rápida",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = roboto,
                color = Color(0xFFDFDCCC),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // FAQs
            AyudaItem(
                titulo = "¿Cómo usar el teleprompter clásico?",
                descripcion = "Selecciona un guion y pulsa 'Teleprompter'. El texto avanza automáticamente al ritmo que elijas en Ajustes."
            )

            AyudaItem(
                titulo = "¿Para qué sirve el modo inteligente?",
                descripcion = "Detecta tu voz y mirada para avanzar automáticamente solo cuando estás leyendo. Ideal para presentaciones naturales."
            )

            AyudaItem(
                titulo = "¿Qué hacer si no avanza el texto?",
                descripcion = "Asegúrate de estar mirando a la pantalla y hablando con claridad. También puedes desactivar el ritmo variable en Ajustes."
            )

            AyudaItem(
                titulo = "¿Cómo cambio el fondo o tamaño de letra?",
                descripcion = "En Ajustes puedes personalizar color de fondo, tamaño de fuente y ritmo de lectura."
            )

            AyudaItem(
                titulo = "¿Qué indican los colores del texto?",
                descripcion = "El texto activo se resalta. Las palabras ya leídas se marcan con otro color para ayudarte a seguir el ritmo."
            )

            AyudaItem(
                titulo = "¿Cómo gestionar mis guiones?",
                descripcion = "Desde la sección 'Guiones' puedes crear, editar, eliminar o importar archivos .txt. Cada guion se guarda con tu cuenta, y puedes seleccionarlo para usarlo en el teleprompter."
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AyudaItem(titulo: String, descripcion: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = titulo,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = roboto,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = descripcion,
            fontSize = 16.sp,
            fontFamily = roboto,
            color = Color.LightGray
        )
    }
}
