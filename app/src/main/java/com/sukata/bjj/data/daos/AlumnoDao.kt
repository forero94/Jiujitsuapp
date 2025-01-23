package com.sukata.bjj.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sukata.bjj.data.entities.AlumnoEntity

@Dao
interface AlumnoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg alumnos: AlumnoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlumno(alumno: AlumnoEntity)

    @Update
    suspend fun updateAlumno(alumno: AlumnoEntity)

    @Delete
    suspend fun deleteAlumno(alumno: AlumnoEntity)

    @Query("SELECT * FROM Alumnos")
    fun getAllAlumnos(): LiveData<List<AlumnoEntity>>

    @Query("SELECT COUNT(*) FROM Alumnos")
    fun count(): Int

    @Query("""
        SELECT * FROM Alumnos
        WHERE (:nombre IS NULL OR nombre LIKE '%' || :nombre || '%')
          AND (:abonado IS NULL OR abonado = :abonado)
    """)
    fun getAlumnosFiltrados(
        nombre: String? = null,
        abonado: Boolean? = null
    ): LiveData<List<AlumnoEntity>>
}
