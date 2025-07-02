package com.example.promptiq.ui.utils.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptiq.R
import com.example.promptiq.ui.theme.roboto
import com.example.promptiq.viewmodel.LoginViewModel

@Composable
fun RegistroScreen(
    viewModel: LoginViewModel,
    onRegistroExitoso: (String, String) -> Unit,
    onVolver: () -> Unit

) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Crear Cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = roboto,
            color = Color(0xFFDFDCCC)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre", color = colorResource(id = R.color.accent)) },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = roboto,
                color = colorResource(id = R.color.accent)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico", color = colorResource(id = R.color.accent)) },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = roboto,
                color = colorResource(id = R.color.accent)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contraseña,
            onValueChange = { contraseña = it },
            label = { Text("Contraseña", color = colorResource(id = R.color.accent)) },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = roboto,
                color = colorResource(id = R.color.accent)
            ),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.registrar(
                    nombre,
                    email,
                    contraseña,
                    onSuccess = {
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        onRegistroExitoso(nombre, email)
                    },
                    onError = { mensaje ->
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A5A91))
        ) {
            Text(
                "REGISTRARSE",
                fontFamily = roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFDFDCCC)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onVolver) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = Color(0xFFDFDCCC))
        }
    }
}
