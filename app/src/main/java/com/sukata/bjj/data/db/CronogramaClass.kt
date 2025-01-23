package com.sukata.bjj.data.db

import com.sukata.bjj.data.entities.TecnicaEntity

data class CronogramaMes(
    val mes: String,
    val semanas: List<CronogramaSemana>
)

data class CronogramaSemana(
    val semana: String,
    val tecnicas: List<TecnicaEntity>
)
