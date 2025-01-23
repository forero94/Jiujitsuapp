package com.sukata.bjj.data.db

import android.util.Log
import java.util.*

fun obtenerMesYSemanaActual(locale: Locale = Locale("es")): Pair<String, String> {
    val calendar = Calendar.getInstance()
    val mes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale)?.lowercase(locale) ?: ""
    val semana = calendar.get(Calendar.WEEK_OF_MONTH).toString() // Solo el número, sin "Semana"
    Log.d("CronogramaFragment", "Fecha actual normalizada: mes=$mes, semana=$semana")
    return Pair(mes, semana)
}


