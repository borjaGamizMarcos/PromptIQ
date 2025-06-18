package com.example.promptiq.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.promptiq.viewmodel.GuionViewModel
import java.text.SimpleDateFormat


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

    Box(modifier = Modifier.fillMaxSize().background(colorResource(id = R.color.background))) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F))
            .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                // Flecha alineada a la izquierda
                IconButton(
                    onClick = { onVolver() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFFDFDCCC)
                    )
                }

                // Logo centrado
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
                text = "Mis Guiones",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = roboto,
                color = Color(0xFFDFDCCC)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Encabezado tabla
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text("Título", color = Color(0xFFDFDCCC), modifier = Modifier.weight(1f))
                Text("Fecha", color = Color(0xFFDFDCCC), modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(48.dp)) // espacio para iconos
            }

            // Lista
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(guiones) { guion ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onEditarGuion(guion) }
                    ) {
                        Text(
                            text = guion.titulo,
                            color = Color(0xFFDFDCCC),
                            fontSize = 18.sp,
                            fontFamily = roboto,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = java.text.SimpleDateFormat("dd/MM/yyyy").format(guion.fechaCreacion),
                            color = Color(0xFFDFDCCC),
                            fontSize = 16.sp,
                            fontFamily = roboto,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { onEditarGuion(guion) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFFDFDCCC))
                        }
                        IconButton(onClick = { guionAEliminar = guion }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFDFDCCC))
                        }
                    }
                    Divider(color = Color(0xFFDFDCCC).copy(alpha = 0.2f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Nuevo Guión
            Button(
                onClick = { onEditarGuion(null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A5A91))
            ) {
                Text(
                    text= "Nuevo Guión",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = roboto,
                    color = Color(0xFFDFDCCC)
                )
            }
        }

        // Diálogo de confirmación
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
                    }) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { guionAEliminar = null }) { Text("Cancelar") }
                }
            )
        }
    }
}

