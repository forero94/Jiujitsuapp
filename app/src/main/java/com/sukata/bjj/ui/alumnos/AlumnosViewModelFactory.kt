package com.sukata.bjj.ui.alumnos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sukata.bjj.data.daos.AlumnoDao

class AlumnosViewModelFactory(private val alumnoDao: AlumnoDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlumnosViewModel::class.java)) {
            return AlumnosViewModel(alumnoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
