package com.example.promptiq.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferenciasDao {

    @Query("SELECT * FROM preferencias_usuario WHERE usuarioEmail = :email LIMIT 1")
    fun obtenerPreferencias(email: String): Flow<PreferenciasUsuario?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPreferencias(preferencias: PreferenciasUsuario)

}
