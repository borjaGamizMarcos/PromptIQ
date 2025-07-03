package com.example.promptiq

import android.app.Application
import android.content.Context
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
import com.example.promptiq.ui.screens.SpeechRecognitionComposable
import com.example.promptiq.ui.screens.TeleprompterInteligenteScreen
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
import com.example.promptiq.ui.screens.AyudaScreen
import com.example.promptiq.ui.utils.screens.RegistroScreen
import com.example.promptiq.ui.screens.RecuperarContraseñaScreen
import com.example.promptiq.utils.FileUtils

class  MainActivity : ComponentActivity() {

    enum class Screen {
        HOME, GUIONES, AJUSTES, CAMBIAR_CONTRASENA, TELEPROMPTER, ADAPTATIVE, AYUDA, REGISTRO, RECUPERAR_CONTRASENA
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PromptIQTheme {
                val context = LocalContext.current
                val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(context.applicationContext as Application))
                val guionViewModel: GuionViewModel = viewModel(factory = GuionViewModelFactory(context.applicationContext as Application))

                // ✅ Cargar sesión almacenada si existe
                val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                var isLoggedIn by remember { mutableStateOf(sharedPreferences.getBoolean("isLoggedIn", false)) }
                var userName by remember { mutableStateOf(sharedPreferences.getString("userName", "") ?: "") }
                var userEmail by remember { mutableStateOf(sharedPreferences.getString("userEmail", "") ?: "") }

                var currentScreen by remember { mutableStateOf(Screen.HOME) }
                var guionEnEdicion by remember { mutableStateOf<Guion?>(null) }
                var mostrarFormulario by remember { mutableStateOf(false) }

                var tamañoFuente by rememberSaveable { mutableStateOf(20f) }
                var colorFondo by rememberSaveable { mutableStateOf("Oscuro") }
                var ritmoVariable by rememberSaveable { mutableStateOf(true) }
                var ritmoLectura by rememberSaveable { mutableStateOf(1f) }

                val ajustesViewModel: AjustesViewModel = viewModel(factory = AjustesViewModelFactory(context.applicationContext as Application))
                var guionSeleccionado by remember { mutableStateOf<Guion?>(null) }

                val seleccionarArchivoLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let {
                        val contenido = FileUtils.leerContenidoDesdeUri(context, it)
                        if (contenido.isNotBlank()) {
                            val nuevoGuion = Guion(
                                titulo = "Importado ${System.currentTimeMillis()}",
                                contenido = contenido,
                                usuarioEmail = userEmail
                            )
                            guionViewModel.insertarGuion(nuevoGuion)
                            Toast.makeText(context, "Guion importado correctamente", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No se pudo leer el archivo", Toast.LENGTH_SHORT).show()
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
                            Screen.HOME -> HomeScreen(
                                userName = userName,
                                onTeleprompterClick = {
                                    val ritmoVariableActivo = ajustesViewModel.ritmoVariable.value
                                    currentScreen = if (ritmoVariableActivo) Screen.ADAPTATIVE else Screen.TELEPROMPTER
                                },
                                onScriptManagementClick = { currentScreen = Screen.GUIONES },
                                onSettingsClick = { currentScreen = Screen.AJUSTES },
                                onHelpClick = { currentScreen = Screen.AYUDA },
                                onLogoutClick = {
                                    loginViewModel.cerrarSesion()
                                    isLoggedIn = false

                                    // ✅ Limpiar sesión guardada
                                    sharedPreferences.edit().clear().apply()
                                }
                            )

                            Screen.GUIONES -> GuionScreen(
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
                                onImportarGuion = { seleccionarArchivoLauncher.launch("*/*") }
                            )

                            Screen.AJUSTES -> AjustesScreen(
                                onVolver = { currentScreen = Screen.HOME },
                                onCambiarContraseña = { currentScreen = Screen.CAMBIAR_CONTRASENA },
                                fuente = ajustesViewModel.fuente.collectAsState().value,
                                onFuenteChange = ajustesViewModel::setFuente,
                                colorFondo = ajustesViewModel.colorFondo.collectAsState().value,
                                onColorFondoChange = ajustesViewModel::setColorFondo,
                                ritmoVariable = ajustesViewModel.ritmoVariable.collectAsState().value,
                                onRitmoVariableChange = ajustesViewModel::setRitmoVariable,
                                ritmoLectura = ajustesViewModel.ritmoLectura.collectAsState().value,
                                onRitmoLecturaChange = ajustesViewModel::setRitmoLectura
                            )

                            Screen.CAMBIAR_CONTRASENA -> CambiarContraseñaScreen(
                                emailUsuario = userEmail,
                                viewModel = loginViewModel,
                                onVolver = { currentScreen = Screen.AJUSTES }
                            )

                            Screen.TELEPROMPTER -> TeleprompterScreen(
                                guiones = guionViewModel.obtenerGuionesPorEmail(userEmail).collectAsState(initial = emptyList()).value,
                                guionSeleccionado = guionSeleccionado,
                                onGuionSeleccionar = { guionSeleccionado = it },
                                fuente = ajustesViewModel.fuente.collectAsState().value,
                                colorFondo = ajustesViewModel.colorFondo.collectAsState().value,
                                ritmoLectura = ajustesViewModel.ritmoLectura.collectAsState().value,
                                onVolver = { currentScreen = Screen.HOME }
                            )

                            Screen.ADAPTATIVE -> TeleprompterInteligenteScreen(
                                guiones = guionViewModel.obtenerGuionesPorEmail(userEmail).collectAsState(initial = emptyList()).value,
                                guionSeleccionado = guionSeleccionado,
                                onGuionSeleccionar = { guionSeleccionado = it },
                                fuente = ajustesViewModel.fuente.collectAsState().value,
                                colorFondo = ajustesViewModel.colorFondo.collectAsState().value,
                                onVolver = { currentScreen = Screen.HOME }
                            )

                            Screen.AYUDA -> AyudaScreen(
                                onVolver = { currentScreen = Screen.HOME }
                            )
                            Screen.REGISTRO -> {}

                            Screen.RECUPERAR_CONTRASENA -> RecuperarContraseñaScreen(
                                viewModel = loginViewModel,
                                onVolver = { currentScreen = Screen.HOME },
                                onRecuperacionExitosa = { currentScreen = Screen.HOME }
                            )


                        }
                    }
                } else {
                    when (currentScreen) {
                        Screen.REGISTRO -> RegistroScreen(
                            viewModel = loginViewModel,
                            onRegistroExitoso = { nombre, email ->
                                userName = nombre
                                userEmail = email
                                isLoggedIn = true
                                ajustesViewModel.cargarPreferencias(email)

                                // Guardar sesión en SharedPreferences
                                sharedPreferences.edit()
                                    .putBoolean("isLoggedIn", true)
                                    .putString("userName", nombre)
                                    .putString("userEmail", email)
                                    .apply()
                                currentScreen = Screen.HOME
                            },
                            onVolver = {
                                currentScreen = Screen.HOME // volver a login
                            }
                        )

                        Screen.RECUPERAR_CONTRASENA -> RecuperarContraseñaScreen(
                            viewModel = loginViewModel,
                            onVolver = { currentScreen = Screen.HOME },
                            onRecuperacionExitosa = { currentScreen = Screen.HOME }
                        )

                        else -> LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = { nombre, email ->
                                userName = nombre
                                userEmail = email
                                isLoggedIn = true
                                ajustesViewModel.cargarPreferencias(email)

                                sharedPreferences.edit()
                                    .putBoolean("isLoggedIn", true)
                                    .putString("userName", nombre)
                                    .putString("userEmail", email)
                                    .apply()
                            },
                            showError = { mensaje ->
                                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                            },
                            onIrARegistro = {
                                currentScreen = Screen.REGISTRO
                            },
                            onIrARecuperarContraseña = {
                                currentScreen = Screen.RECUPERAR_CONTRASENA
                            }
                        )
                    }
                }
            }
        }
    }
}
