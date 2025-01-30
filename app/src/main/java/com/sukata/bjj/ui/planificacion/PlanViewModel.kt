package com.sukata.bjj.ui.planificacion

import android.app.Application
import androidx.lifecycle.*
import com.sukata.bjj.data.db.AppDatabase
import com.sukata.bjj.data.entities.Plan
import com.sukata.bjj.data.repository.PlanRepository
import kotlinx.coroutines.launch

class PlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlanRepository
    val allPlanes: LiveData<List<Plan>>

    init {
        val planDao = AppDatabase.getDatabase(application).planDao()
        repository = PlanRepository(planDao)
        allPlanes = repository.allPlanes
    }

    fun insert(plan: Plan) = viewModelScope.launch {
        repository.insert(plan)
    }

    fun delete(plan: Plan) = viewModelScope.launch {
        repository.delete(plan)
    }

    fun update(plan: Plan) = viewModelScope.launch {
        repository.update(plan)
    }
}

class PlanViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlanViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
