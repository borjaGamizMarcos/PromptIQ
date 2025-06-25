package com.example.promptiq

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.promptiq.data.local.Guion
import com.example.promptiq.ui.screens.CameraFaceDetectionComposable
import com.example.promptiq.ui.screens.AjustesScreen
import com.example.promptiq.ui.screens.GuionFormScreen
import com.example.promptiq.ui.screens.GuionScreen
import com.example.promptiq.ui.screens.CambiarContraseñaScreen
import com.example.promptiq.ui.theme.PromptIQTheme
import com.example.promptiq.ui.utils.screens.HomeScreen
import com.example.promptiq.ui.utils.screens.LoginScreen
import com.example.promptiq.ui.screens.TeleprompterScreen
import com.example.promptiq.viewmodel.AjustesViewModel
import com.example.promptiq.viewmodel.AjustesViewModelFactory
import com.example.promptiq.viewmodel.GuionViewModel
import com.example.promptiq.viewmodel.GuionViewModelFactory
import com.example.promptiq.viewmodel.LoginViewModel
import com.example.promptiq.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {

    enum class Screen {
        HOME, GUIONES, AJUSTES, CAMBIAR_CONTRASENA, TELEPROMPTER, FACE_DETECTION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PromptIQTheme {
                val context = LocalContext.current
                val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(context.applicationContext as Application))
                val guionViewModel: GuionViewModel = viewModel(factory = GuionViewModelFactory(context.applicationContext as Application))



                var isLoggedIn by remember { mutableStateOf(false) }
                var userName by remember { mutableStateOf("") }
                var userEmail by remember { mutableStateOf("") }
                var currentScreen by remember { mutableStateOf(Screen.HOME) }
                var guionEnEdicion by remember { mutableStateOf<Guion?>(null) }
                var mostrarFormulario by remember { mutableStateOf(false) }

                var tamañoFuente by rememberSaveable { mutableStateOf(20f) }
                var colorFondo by rememberSaveable { mutableStateOf("Oscuro") }
                var ritmoVariable by rememberSaveable { mutableStateOf(true) }
                var ritmoLectura by rememberSaveable { mutableStateOf(1f) }
                val ajustesViewModel: AjustesViewModel = viewModel(
                    factory = AjustesViewModelFactory(context.applicationContext as Application)
                )

                var guionSeleccionado by remember { mutableStateOf<Guion?>(null) }



                val seleccionarArchivoLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let {
                        val inputStream = context.contentResolver.openInputStream(it)
                        val contenido = inputStream?.bufferedReader().use { it?.readText() } ?: ""
                        if (contenido.isNotBlank()) {
                            val nuevoGuion = Guion(
                                titulo = "Importado ${System.currentTimeMillis()}",
                                contenido = contenido,
                                usuarioEmail = userEmail
                            )
                            guionViewModel.insertarGuion(nuevoGuion)
                            Toast.makeText(context, "Guion importado correctamente", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "El archivo está vacío", Toast.LENGTH_SHORT).show()
                        }
                    }
                }


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
                                    onTeleprompterClick = { currentScreen = Screen.TELEPROMPTER },
                                    onScriptManagementClick = { currentScreen = Screen.GUIONES },
                                    onSettingsClick = { currentScreen= Screen.AJUSTES },
                                    onHelpClick = { currentScreen= Screen.FACE_DETECTION},
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
                                    },
                                    onImportarGuion = { seleccionarArchivoLauncher.launch("text/plain")}
                                )
                            }
                            Screen.AJUSTES ->
                                AjustesScreen(
                                    onVolver = { currentScreen = Screen.HOME },
                                    onCambiarContraseña = { currentScreen = Screen.CAMBIAR_CONTRASENA},
                                    fuente = ajustesViewModel.fuente.collectAsState().value,
                                    onFuenteChange = ajustesViewModel::setFuente,
                                    colorFondo = ajustesViewModel.colorFondo.collectAsState().value,
                                    onColorFondoChange = ajustesViewModel::setColorFondo,
                                    ritmoVariable = ajustesViewModel.ritmoVariable.collectAsState().value,
                                    onRitmoVariableChange = ajustesViewModel::setRitmoVariable,
                                    ritmoLectura = ajustesViewModel.ritmoLectura.collectAsState().value,
                                    onRitmoLecturaChange = ajustesViewModel::setRitmoLectura
                                )

                            Screen.CAMBIAR_CONTRASENA ->
                                CambiarContraseñaScreen(
                                    onVolver = { currentScreen = Screen.AJUSTES }
                                )

                            Screen.TELEPROMPTER -> {
                                TeleprompterScreen(
                                    guiones = guionViewModel.obtenerGuionesPorEmail(userEmail).collectAsState(initial = emptyList()).value,
                                    guionSeleccionado = guionSeleccionado,
                                    onGuionSeleccionar = { guionSeleccionado = it },
                                    fuente = ajustesViewModel.fuente.collectAsState().value,
                                    colorFondo = ajustesViewModel.colorFondo.collectAsState().value,
                                    ritmoLectura = ajustesViewModel.ritmoLectura.collectAsState().value,
                                    onVolver = { currentScreen = Screen.HOME }
                                )
                            }

                            Screen.FACE_DETECTION -> {
                                CameraFaceDetectionComposable()
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
                            ajustesViewModel.cargarPreferencias(email)
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
