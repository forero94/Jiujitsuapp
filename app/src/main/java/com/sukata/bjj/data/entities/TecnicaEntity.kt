package com.sukata.bjj.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sukata.bjj.data.db.DificultadConverter
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Tecnicas")
@TypeConverters(DificultadConverter::class)
data class TecnicaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val posicionInicial: String,
    val dificultad: Dificultad? = null,
    val tipoTecnica: String,
    val subtipoPosicionInicial: String? = null, // Nuevo campo para el subtipo
    val contexto: String? = null,
    val objetivoPrincipal: String? = null,
    val descripcion: String? = null,
    val variante: String? = null,
    val recursosAdicionales: List<String>? = emptyList()
) : Parcelable
