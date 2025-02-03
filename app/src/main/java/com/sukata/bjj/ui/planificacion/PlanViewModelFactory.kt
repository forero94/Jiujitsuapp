package com.sukata.bjj.ui.planificacion

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlanViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlanViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}