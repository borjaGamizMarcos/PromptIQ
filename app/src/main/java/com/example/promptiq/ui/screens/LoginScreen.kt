package com.example.promptiq.ui.utils.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptiq.R
import com.example.promptiq.ui.theme.roboto
import com.example.promptiq.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (String, String) -> Unit,
    showError: (String) -> Unit,
    onIrARegistro: () -> Unit,
    onIrARecuperarContraseña: () ->Unit

) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_hor),
            contentDescription = "Logo",
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))


        // Eslogan
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 40.dp) // 👈 Ajusta espaciado inferior para separarlo del resto
        ) {
            Text(
                text = "Tu discurso, ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = roboto,
                color = Color(0xFFDFDCCC)
            )
            Text(
                text = "a tu ritmo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = roboto,
                color = Color(0xFF3A5A91)
            )
        }

        //Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    "Correo electrónico",
                    fontFamily = roboto,
                    color = colorResource(id=R.color.accent)
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = roboto,
                color = colorResource(id=R.color.accent)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    "Contraseña",
                    fontFamily = roboto,
                    color = colorResource(id=R.color.accent)
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = roboto,
                color = colorResource(id=R.color.accent)
            ),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.login(
                    email,
                    password,
                    onSuccess = { usuario -> onLoginSuccess(usuario.nombre, usuario.email) },
                    onError = { mensaje -> showError(mensaje) }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),

            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A5A91))
        ) {
            Text("INICIAR SESIÓN", fontFamily = roboto, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFDFDCCC))
        }

        Spacer(modifier = Modifier.height(12.dp))

        ClickableText(
            text = AnnotatedString("¿No tienes cuenta? Regístrate"),
            onClick = { onIrARegistro() },
            style = TextStyle(
                color = Color(0xFFDFDCCC),
                fontSize = 14.sp,
                fontFamily = roboto,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = Color(0xFFDFDCCC),
            fontSize = 14.sp,
            fontFamily = roboto,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { onIrARecuperarContraseña() }

        )

    }
}