package com.sukata.bjj.ui.cronogramas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sukata.bjj.data.daos.CronogramaDao

class CronogramaViewModelFactory(
    private val cronogramaDao: CronogramaDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CronogramaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CronogramaViewModel(cronogramaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
