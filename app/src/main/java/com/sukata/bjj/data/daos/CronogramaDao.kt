package com.sukata.bjj.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sukata.bjj.data.entities.CronogramaEntity

@Dao
interface CronogramaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg cronogramas: CronogramaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCronograma(cronograma: CronogramaEntity)

    @Update
    suspend fun updateCronograma(cronograma: CronogramaEntity)

    @Delete
    suspend fun deleteCronograma(cronograma: CronogramaEntity)

    @Query("SELECT COUNT(*) FROM Cronogramas")
    fun count(): Int

    @Query("SELECT * FROM Cronogramas")
    fun getAllCronogramas(): LiveData<List<CronogramaEntity>>

    @Query("""
    SELECT * FROM Cronogramas
    WHERE date(:currentDate) BETWEEN 
          date(fecha, '-7 days') AND date(fecha)
    LIMIT 1
""")
    fun getTecnicaSemanaActual(currentDate: String): LiveData<CronogramaEntity>

}
