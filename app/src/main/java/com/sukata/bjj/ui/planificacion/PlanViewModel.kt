package com.sukata.bjj.ui.planificacion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
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

    // Función para bifurcar un plan
    fun bifurcar(
        planOriginal: Plan,
        nuevaPosicion: String,
        nuevasAcciones: List<String>,
        nuevasReacciones: List<String>
    ) = viewModelScope.launch {
        // Actualizamos (o guardamos) el plan original, si es necesario
        repository.update(planOriginal)
        // Creamos el nuevo plan tomando la nueva posición como primeraPosicion
        val nuevoPlan = Plan(
            primeraPosicion = nuevaPosicion,
            acciones = nuevasAcciones,
            reacciones = nuevasReacciones
        )
        repository.insert(nuevoPlan)
    }
}
