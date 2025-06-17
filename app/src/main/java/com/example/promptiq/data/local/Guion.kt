package com.example.promptiq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guiones")
data class Guion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioEmail: String,
    val titulo: String,
    val contenido: String,
    val fechaCreacion: Long = System.currentTimeMillis()
)
