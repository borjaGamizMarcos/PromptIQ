package com.example.promptiq

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.promptiq.ui.theme.PromptIQTheme
import com.example.promptiq.ui.utils.screens.LoginScreen
import com.example.promptiq.ui.utils.screens.HomeScreen
import com.example.promptiq.viewmodel.LoginViewModel
import com.example.promptiq.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PromptIQTheme {
                val context = LocalContext.current
                val viewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(context.applicationContext as Application)
                )

                var isLoggedIn by remember { mutableStateOf(false) }
                var userName by remember { mutableStateOf("") }

                if (isLoggedIn) {
                    HomeScreen(
                        userName = userName,
                        onTeleprompterClick = { /* TODO */ },
                        onScriptManagementClick = { /* TODO */ },
                        onSettingsClick = { /* TODO */ },
                        onHelpClick = { /* TODO */ },
                        onLogoutClick = {
                            viewModel.cerrarSesion()
                            isLoggedIn = false
                        }
                    )

                } else {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { nombre, _ ->
                            userName = nombre
                            isLoggedIn = true
                        },
                        showError = { mensaje ->
                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
