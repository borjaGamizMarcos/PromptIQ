package com.example.promptiq.data.repository

import com.example.promptiq.data.local.PreferenciasDao
import com.example.promptiq.data.local.PreferenciasUsuario
import kotlinx.coroutines.flow.Flow

class PreferenciasRepository(private val dao: PreferenciasDao) {

    fun obtenerPreferenciasPorEmail(email: String): Flow<PreferenciasUsuario?> {
        return dao.obtenerPreferencias(email)
    }

    suspend fun actualizarPreferencias(preferencias: PreferenciasUsuario) {
        dao.insertarPreferencias(preferencias)
    }
}