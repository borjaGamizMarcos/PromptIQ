package com.example.promptiq.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.promptiq.data.local.AppDatabase
import com.example.promptiq.data.local.PreferenciasUsuario
import com.example.promptiq.data.repository.PreferenciasRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AjustesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PreferenciasRepository =
        PreferenciasRepository(AppDatabase.getDatabase(application).preferenciasDao())

    private val _usuarioEmail = MutableStateFlow<String?>(null)
    val usuarioEmail: StateFlow<String?> = _usuarioEmail

    private val _preferencias = MutableStateFlow<PreferenciasUsuario?>(
        PreferenciasUsuario("", fuente = 20f, colorFondo = "Oscuro", ritmoVariable = true, ritmoLectura = 1f)
    )

    // Exponer valores individuales
    val fuente: StateFlow<Float> = _preferencias.map { it?.fuente ?: 20f }.stateIn(viewModelScope, SharingStarted.Eagerly, 20f)
    val colorFondo: StateFlow<String> = _preferencias.map { it?.colorFondo ?: "Oscuro" }.stateIn(viewModelScope, SharingStarted.Eagerly, "Oscuro")
    val ritmoVariable: StateFlow<Boolean> = _preferencias.map { it?.ritmoVariable ?: true }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val ritmoLectura: StateFlow<Float> = _preferencias.map { it?.ritmoLectura ?: 1f }.stateIn(viewModelScope, SharingStarted.Eagerly, 1f)

    fun cargarPreferencias(email: String) {
        _usuarioEmail.value = email
        viewModelScope.launch {
            repository.obtenerPreferenciasPorEmail(email).collect { prefs ->
                _preferencias.value = prefs ?: PreferenciasUsuario(
                    usuarioEmail = email,
                    fuente = 20f,
                    colorFondo = "Oscuro",
                    ritmoVariable = true,
                    ritmoLectura = 1f
                )
            }
        }
    }

    private fun guardar() {
        val email = _usuarioEmail.value ?: return
        _preferencias.value?.copy(usuarioEmail = email)?.let {
            viewModelScope.launch {
                repository.actualizarPreferencias(it)
            }
        }
    }

    // Métodos para actualizar preferencias
    fun setFuente(nuevaFuente: Float) {
        _preferencias.value = _preferencias.value?.copy(fuente = nuevaFuente)
        guardar()
    }

    fun setColorFondo(nuevoColor: String) {
        _preferencias.value = _preferencias.value?.copy(colorFondo = nuevoColor)
        guardar()
    }

    fun setRitmoVariable(nuevoValor: Boolean) {
        _preferencias.value = _preferencias.value?.copy(ritmoVariable = nuevoValor)
        guardar()
    }

    fun setRitmoLectura(nuevoValor: Float) {
        _preferencias.value = _preferencias.value?.copy(ritmoLectura = nuevoValor)
        guardar()
    }
}
