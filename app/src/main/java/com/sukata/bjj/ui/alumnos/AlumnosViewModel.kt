package com.sukata.bjj.ui.alumnos

import android.app.Application
import androidx.lifecycle.*
import com.sukata.bjj.data.daos.AlumnoDao
import com.sukata.bjj.data.db.AppDatabase
import com.sukata.bjj.data.entities.AlumnoEntity
import kotlinx.coroutines.launch

class AlumnosViewModel(private val alumnoDao: AlumnoDao) : ViewModel() {

    val allAlumnos: LiveData<List<AlumnoEntity>> = alumnoDao.getAllAlumnos()

    private val _filtro = MutableLiveData<Filtro>()

    // Clase para agrupar filtros
    data class Filtro(
        val nombre: String? = null,
        val abonado: Boolean? = null
    )
    val alumnosFiltrados: LiveData<List<AlumnoEntity>> = _filtro.switchMap { filtro ->
        alumnoDao.getAlumnosFiltrados(
            nombre = filtro.nombre,
            abonado = filtro.abonado
        )
    }
    init {
        // Inicializar el filtro vacío (sin filtrar)
        _filtro.value = Filtro()
    }
    // Función para insertar un nuevo alumno
    fun insert(alumno: AlumnoEntity) {
        viewModelScope.launch {
            alumnoDao.insertAlumno(alumno)
        }
    }
    fun setFiltro(nombre: String? = null, abonado: Boolean? = null) {
        _filtro.value = Filtro(nombre, abonado)
    }
    fun limpiarFiltros() {
        _filtro.value = Filtro()
    }

    // Función para actualizar un alumno existente
    fun updateAlumno(alumno: AlumnoEntity) {
        viewModelScope.launch {
            alumnoDao.updateAlumno(alumno)
        }
    }


    // Función para eliminar un alumno
    fun delete(alumno: AlumnoEntity) = viewModelScope.launch {
        alumnoDao.deleteAlumno(alumno)
    }


}
