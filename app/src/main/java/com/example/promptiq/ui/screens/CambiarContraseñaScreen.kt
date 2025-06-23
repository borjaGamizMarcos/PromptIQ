package com.example.promptiq.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptiq.R
import com.example.promptiq.ui.theme.roboto

@Composable
fun CambiarContraseñaScreen(
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    var contraseñaActual by remember { mutableStateOf("") }
    var nuevaContraseña by remember { mutableStateOf("") }
    var repetirContraseña by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado con flecha y logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { onVolver() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFFDFDCCC)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_hor),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(48.dp)
                    .padding(end = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Cambiar contraseña", fontSize = 24.sp, color = Color(0xFFDFDCCC), fontFamily = roboto)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = contraseñaActual,
            onValueChange = { contraseñaActual = it },
            label = { Text("Contraseña actual") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nuevaContraseña,
            onValueChange = { nuevaContraseña = it },
            label = { Text("Nueva contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repetirContraseña,
            onValueChange = { repetirContraseña = it },
            label = { Text("Repetir nueva contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (nuevaContraseña != repetirContraseña) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show()
                    onVolver()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A5A91))
        ) {
            Text("Guardar cambios", color = Color(0xFFDFDCCC))
        }
    }
}
