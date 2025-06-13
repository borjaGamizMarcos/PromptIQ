package com.example.promptiq.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.promptiq.data.Usuario

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarios = listOf(
        Usuario("alex@example.com", "1234", "Alex"),
        Usuario("borja@example.com", "abcd", "Borja"),
        Usuario("sofia@example.com", "pass", "Sofía")
    )

    private val sharedPrefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    fun login(
        email: String,
        contraseña: String,
        onSuccess: (Usuario) -> Unit,
        onError: (String) -> Unit
    ) {
        val usuario = usuarios.find { it.email == email && it.contraseña == contraseña }
        if (usuario != null) {
            sharedPrefs.edit().putString("usuario_email", usuario.email).apply()
            onSuccess(usuario)
        } else {
            onError("Email o contraseña incorrectos.")
        }
    }

    fun obtenerUsuarioActivo(): Usuario? {
        val email = sharedPrefs.getString("usuario_email", null)
        return usuarios.find { it.email == email }
    }

    fun cerrarSesion() {
        sharedPrefs.edit().remove("usuario_email").apply()
    }
}
