package com.example.promptiq.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GuionDao {

    @Query("SELECT * FROM guiones WHERE usuarioEmail = :email ORDER BY fechaCreacion DESC")
    fun obtenerGuionesPorEmail(email: String): Flow<List<Guion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarGuion(guion: Guion)

    @Update
    suspend fun actualizarGuion(guion: Guion)

    @Delete
    suspend fun eliminarGuion(guion: Guion)
}
