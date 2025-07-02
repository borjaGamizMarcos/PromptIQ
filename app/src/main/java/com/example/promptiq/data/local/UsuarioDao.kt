package com.example.promptiq.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE email = :email AND contraseña = :contraseña")
    suspend fun login(email: String, contraseña: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun obtenerUsuarioPorEmail(email: String): Usuario?

    @Query("UPDATE usuarios SET contraseña = :nuevaContraseña WHERE email = :email")
    suspend fun cambiarContraseña(email: String, nuevaContraseña: String)

    @Query("SELECT * FROM usuarios WHERE email = :email AND contraseña = :contraseña")
    suspend fun verificarCredenciales(email: String, contraseña: String): Usuario?

}
