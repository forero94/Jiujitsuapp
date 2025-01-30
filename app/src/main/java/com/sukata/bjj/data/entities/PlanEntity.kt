// Plan.kt
package com.sukata.bjj.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planes")
data class Plan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val primeraPosicion: String,
    val acciones: List<String>,
    val reacciones: List<String>
)