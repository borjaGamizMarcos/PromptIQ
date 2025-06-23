package com.example.promptiq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferencias_usuario")
data class PreferenciasUsuario(
    @PrimaryKey val usuarioEmail: String,
    val fuente: Float,
    val colorFondo: String,
    val ritmoVariable: Boolean,
    val ritmoLectura: Float
)
