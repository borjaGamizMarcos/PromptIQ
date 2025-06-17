package com.example.promptiq.data.repository

import com.example.promptiq.data.local.Guion
import com.example.promptiq.data.local.GuionDao
import kotlinx.coroutines.flow.Flow

class GuionRepository(private val dao: GuionDao) {

    fun obtenerGuionesPorEmail(email: String): Flow<List<Guion>> {
        return dao.obtenerGuionesPorEmail(email)
    }

    suspend fun insertarGuion(guion: Guion) {
        dao.insertarGuion(guion)
    }

    suspend fun eliminarGuion(guion: Guion) {
        dao.eliminarGuion(guion)
    }
}
