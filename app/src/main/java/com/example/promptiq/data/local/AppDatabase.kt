package com.example.promptiq.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Guion::class, PreferenciasUsuario::class], // <-- AÑADE ESTO
    version = 2
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun guionDao(): GuionDao
    abstract fun preferenciasDao() : PreferenciasDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "promptiq_database"
                )
                    .fallbackToDestructiveMigration(true) // ⬅️ ¡Esto es lo que evita el crash!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
