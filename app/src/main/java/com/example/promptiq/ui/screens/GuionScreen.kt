package com.example.promptiq.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.promptiq.data.local.Guion
import com.example.promptiq.viewmodel.GuionViewModel

@Composable
fun GuionScreen(
    userEmail: String,
    viewModel: GuionViewModel,
    onEditarGuion: (Guion?) -> Unit,
    onVolver: () -> Unit,
    onMostrarMensaje: (String) -> Unit
) {
    val guiones by viewModel.obtenerGuionesPorEmail(userEmail).collectAsState(initial = emptyList())
    var guionAEliminar by remember { mutableStateOf<Guion?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mis Guiones", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onEditarGuion(null) }, modifier = Modifier.fillMaxWidth()) {
            Text("Nuevo guion")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(guiones) { guion ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onEditarGuion(guion) }) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(guion.titulo, style = MaterialTheme.typography.titleMedium)
                            Text("Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(guion.fechaCreacion)}", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { guionAEliminar = guion }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al menú")
        }
    }

    guionAEliminar?.let { guion ->
        AlertDialog(
            onDismissRequest = { guionAEliminar = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que quieres eliminar este guion?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarGuion(guion)
                    guionAEliminar = null
                    onMostrarMensaje("Guion eliminado correctamente.")
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { guionAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
