package com.sukata.bjj.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "Cronogramas")
data class CronogramaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val fecha: String, // Esto debe ser compatible con el DateConverter
    val descripcion: String
)
