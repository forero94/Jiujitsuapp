package com.sukata.bjj.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sukata.bjj.data.db.ClasesPorSemana
import com.sukata.bjj.data.db.ClasesPorSemanaConverter
import com.sukata.bjj.data.db.DateConverter
import com.sukata.bjj.data.db.DificultadConverter
import java.util.Date

@Entity(tableName = "Alumnos")
@TypeConverters(DificultadConverter::class, DateConverter::class, ClasesPorSemanaConverter::class)
data class AlumnoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idAlumno: String,
    val nombre: String,
    val cinturon: String?,
    val abonado: Boolean?,
    val celular: String?,
    val fechaNacimiento: Date?,
    val fechaAfiliacion: Date?,
    val clasesPorSemana: ClasesPorSemana
) {
    // Propiedad calculada para la edad
    val edad: Int?
        get() = fechaNacimiento?.let { calcularEdad(it) }

    private fun calcularEdad(fechaNacimiento: Date): Int {
        val hoy = java.util.Calendar.getInstance()
        val nacimiento = java.util.Calendar.getInstance()
        nacimiento.time = fechaNacimiento

        var edad = hoy.get(java.util.Calendar.YEAR) - nacimiento.get(java.util.Calendar.YEAR)
        if (hoy.get(java.util.Calendar.DAY_OF_YEAR) < nacimiento.get(java.util.Calendar.DAY_OF_YEAR)) {
            edad--
        }
        return edad
    }
}
