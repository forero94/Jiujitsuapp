package com.sukata.bjj.data.entities

import androidx.room.Entity

@Entity(
    tableName = "FiltrosSemanales",
    primaryKeys = ["mes", "semana"]
)
data class FiltroSemanalEntity(
    val mes: String,
    val semana: String,
    val posicionInicial: String?,  // Puede ser nulo si no se selecciona
    val tipoTecnica: String?       // Puede ser nulo si no se selecciona
)
