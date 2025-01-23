package com.sukata.bjj.data.db

import androidx.room.TypeConverter

enum class ClasesPorSemana {
    UNA, DOS, TRES, LIBRE
}

class ClasesPorSemanaConverter {
    @TypeConverter
    fun fromClasesPorSemana(clases: ClasesPorSemana?): String? {
        return clases?.name
    }

    @TypeConverter
    fun toClasesPorSemana(value: String?): ClasesPorSemana? {
        return value?.let { ClasesPorSemana.valueOf(it) }
    }
}
