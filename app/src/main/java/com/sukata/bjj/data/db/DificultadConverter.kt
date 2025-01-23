package com.sukata.bjj.data.db

import androidx.room.TypeConverter
import com.sukata.bjj.data.entities.Dificultad

class DificultadConverter {

    @TypeConverter
    fun fromDificultad(dificultad: Dificultad?): String? {
        return dificultad?.name // Devuelve null si dificultad es null
    }

    @TypeConverter
    fun toDificultad(valor: String?): Dificultad? {
        return valor?.let {
            try {
                Dificultad.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null // o un valor por defecto
            }
        }
    }
}
