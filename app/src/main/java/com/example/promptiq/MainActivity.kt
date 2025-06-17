package com.example.promptiq

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.promptiq.data.local.Guion
import com.example.promptiq.ui.screens.GuionFormScreen
import com.example.promptiq.ui.screens.GuionScreen
import com.example.promptiq.ui.theme.PromptIQTheme
import com.example.promptiq.ui.utils.screens.HomeScreen
import com.example.promptiq.ui.utils.screens.LoginScreen
import com.example.promptiq.viewmodel.GuionViewModel
import com.example.promptiq.viewmodel.GuionViewModelFactory
import com.example.promptiq.viewmodel.LoginViewModel
import com.example.promptiq.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {

    enum class Screen {
        HOME, GUIONES
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PromptIQTheme {
                val context = LocalContext.current
                val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(context.applicationContext as Application))
                val guionViewModel: GuionViewModel = viewModel(factory = GuionViewModelFactory(context.applicationContext as Application))
                val snackbarHostState = remember { SnackbarHostState() }

                var isLoggedIn by remember { mutableStateOf(false) }
                var userName by remember { mutableStateOf("") }
                var userEmail by remember { mutableStateOf("") }
                var currentScreen by remember { mutableStateOf(Screen.HOME) }
                var guionEnEdicion by remember { mutableStateOf<Guion?>(null) }
                var mostrarFormulario by remember { mutableStateOf(false) }


                if (isLoggedIn) {
                    if (mostrarFormulario) {
                        GuionFormScreen(
                            guion = guionEnEdicion ?: Guion(usuarioEmail = userEmail, titulo = "", contenido = ""),
                            onGuardar = { guion ->
                                if (guion.id == 0) {
                                    guionViewModel.insertarGuion(guion.copy(usuarioEmail = userEmail))
                                } else {
                                    guionViewModel.actualizarGuion(guion.copy(usuarioEmail = userEmail))
                                }
                                guionEnEdicion = null
                                mostrarFormulario = false
                            },
                            onCancelar = {
                                guionEnEdicion = null
                                mostrarFormulario = false
                            }
                        )
                    } else {
                        when (currentScreen) {
                            Screen.HOME -> {
                                HomeScreen(
                                    userName = userName,
                                    onTeleprompterClick = { /* TODO */ },
                                    onScriptManagementClick = { currentScreen = Screen.GUIONES },
                                    onSettingsClick = { /* TODO */ },
                                    onHelpClick = { /* TODO */ },
                                    onLogoutClick = {
                                        loginViewModel.cerrarSesion()
                                        isLoggedIn = false
                                    }
                                )
                            }

                            Screen.GUIONES -> {
                                GuionScreen(
                                    viewModel = guionViewModel,
                                    userEmail = userEmail,
                                    onEditarGuion = { guion ->
                                        guionEnEdicion = guion
                                        mostrarFormulario = true
                                    },
                                    onVolver = { currentScreen = Screen.HOME },
                                    onMostrarMensaje = { mensaje ->
                                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }


            } else {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = { nombre, email ->
                            userName = nombre
                            userEmail = email
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
