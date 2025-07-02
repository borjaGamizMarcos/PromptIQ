package com.example.promptiq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val email: String,
    val contraseña: String,
    val nombre: String
)
