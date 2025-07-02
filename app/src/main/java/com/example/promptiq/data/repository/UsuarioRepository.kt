package com.example.promptiq.repository

import com.example.promptiq.data.local.Usuario
import com.example.promptiq.data.local.UsuarioDao

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            usuarioDao.insertarUsuario(usuario)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, contraseña: String): Usuario? {
        return usuarioDao.login(email, contraseña)
    }

    suspend fun obtenerUsuario(email: String): Usuario? {
        return usuarioDao.obtenerUsuarioPorEmail(email)
    }

    suspend fun cambiarContraseña(email: String, nuevaContraseña: String) {
        usuarioDao.cambiarContraseña(email, nuevaContraseña)
    }

    suspend fun verificarCredenciales(email: String, contraseña: String): Boolean {
        return usuarioDao.verificarCredenciales(email, contraseña) != null
    }


}
