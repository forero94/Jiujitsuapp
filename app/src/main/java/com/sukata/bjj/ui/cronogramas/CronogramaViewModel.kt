package com.sukata.bjj.ui.cronogramas

import androidx.lifecycle.*
import com.sukata.bjj.data.daos.CronogramaDao
import com.sukata.bjj.data.entities.CronogramaEntity

class CronogramaViewModel(private val cronogramaDao: CronogramaDao) : ViewModel() {

    fun getTecnicaSemanaActual(currentDate: String): LiveData<CronogramaEntity> {
        return cronogramaDao.getTecnicaSemanaActual(currentDate)
    }
}

