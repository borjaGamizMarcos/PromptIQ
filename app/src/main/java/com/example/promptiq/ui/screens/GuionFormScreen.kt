package com.example.promptiq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.promptiq.data.local.Guion

@Composable
fun GuionFormScreen(
    guion: Guion?,
    onGuardar: (Guion) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf(guion?.titulo ?: "") }
    var contenido by remember { mutableStateOf(guion?.contenido ?: "") }


    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = if (guion == null) "Nuevo Guion" else "Editar Guion",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contenido,
            onValueChange = { contenido = it },
            label = { Text("Contenido") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onCancelar) {
                Text("Cancelar")
            }

            Button(onClick = {
                val guionFinal = guion?.copy(titulo = titulo, contenido = contenido)
                    ?: Guion(titulo = titulo, contenido = contenido, usuarioEmail = "")
                onGuardar(guionFinal)
            }) {
                Text("Guardar")
            }
        }
    }
}
