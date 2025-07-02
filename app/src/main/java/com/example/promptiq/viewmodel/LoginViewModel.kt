// com/example/promptiq/viewmodel/LoginViewModel.kt
package com.example.promptiq.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.promptiq.data.local.AppDatabase
import com.example.promptiq.data.local.Usuario
import com.example.promptiq.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository: UsuarioRepository
    private val sharedPrefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    init {
        val dao = AppDatabase.getDatabase(application).usuarioDao()
        usuarioRepository = UsuarioRepository(dao)
    }

    fun login(
        email: String,
        contraseña: String,
        onSuccess: (Usuario) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val usuario = withContext(Dispatchers.IO) {
                usuarioRepository.login(email, contraseña)
            }

            if (usuario != null) {
                sharedPrefs.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("userEmail", usuario.email)
                    .putString("userName", usuario.nombre)
                    .apply()
                onSuccess(usuario)
            } else {
                onError("Email o contraseña incorrectos.")
            }
        }
    }

    fun registrar(
        nombre: String,
        email: String,
        contraseña: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val existe = withContext(Dispatchers.IO) {
                usuarioRepository.obtenerUsuario(email)
            }

            if (existe != null) {
                onError("Ya existe un usuario con ese correo.")
                return@launch
            }

            val nuevoUsuario = Usuario(nombre = nombre, email = email, contraseña = contraseña)

            val resultado = withContext(Dispatchers.IO) {
                usuarioRepository.registrarUsuario(nuevoUsuario)
            }

            if (resultado.isSuccess) {
                onSuccess()
            } else {
                onError("Error al registrar el usuario.")
            }
        }
    }

    fun cerrarSesion() {
        sharedPrefs.edit().clear().apply()
    }

    fun cambiarContraseña(email: String, nuevaContraseña: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                usuarioRepository.cambiarContraseña(email, nuevaContraseña)
                onSuccess()
            } catch (e: Exception) {
                onError("Error al cambiar la contraseña")
            }
        }
    }
    fun verificarCredenciales(
        email: String,
        contraseña: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            val esValido = usuarioRepository.verificarCredenciales(email, contraseña)
            if (esValido) {
                onSuccess()
            } else {
                onError()
            }
        }
    }


}
