package com.example.promptiq.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.promptiq.data.local.AppDatabase
import com.example.promptiq.data.local.Guion
import com.example.promptiq.data.repository.GuionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GuionViewModel(application: Application) : ViewModel() {
    private val repository: GuionRepository

    init {
        val guionDao = AppDatabase.getDatabase(application).guionDao()
        repository = GuionRepository(guionDao)
    }

    fun obtenerGuionesPorEmail(email: String): Flow<List<Guion>> {
        return repository.obtenerGuionesPorEmail(email)
    }

    fun insertarGuion(guion: Guion) = viewModelScope.launch {
        repository.insertarGuion(guion)
    }

    fun actualizarGuion(guion: Guion) = viewModelScope.launch {
        repository.insertarGuion(guion)
    }

    fun eliminarGuion(guion: Guion) = viewModelScope.launch {
        repository.eliminarGuion(guion)
    }

}
