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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptiq.R
import com.example.promptiq.ui.theme.roboto
import com.example.promptiq.viewmodel.LoginViewModel

@Composable
fun RecuperarContraseñaScreen(
    viewModel: LoginViewModel,
    onVolver: () -> Unit,
    onRecuperacionExitosa: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var nuevaContraseña by remember { mutableStateOf("") }
    var repetirContraseña by remember { mutableStateOf("") }
    val textColor = Color(0xFFDFDCCC)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Recuperar Contraseña",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = roboto,
            color = Color(0xFFDFDCCC)
        )


        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            textStyle = TextStyle(color = textColor, fontFamily = roboto),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nuevaContraseña,
            onValueChange = { nuevaContraseña = it },
            label = { Text("Nueva contraseña") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = textColor, fontFamily = roboto),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repetirContraseña,
            onValueChange = { repetirContraseña = it },
            label = { Text("Repetir contraseña") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = textColor, fontFamily = roboto),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (nuevaContraseña != repetirContraseña) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.cambiarContraseña(email, nuevaContraseña,
                        onSuccess = {
                            Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                            onRecuperacionExitosa()
                        },
                        onError = { mensaje ->
                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A5A91))
        ) {
            Text("Restablecer", color = Color(0xFFDFDCCC))
        }
    }
}
