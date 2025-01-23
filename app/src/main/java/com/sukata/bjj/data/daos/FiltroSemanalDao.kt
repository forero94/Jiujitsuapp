package com.sukata.bjj.data.daos

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sukata.bjj.data.entities.FiltroSemanalEntity

@Dao
interface FiltroSemanalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiltro(filtro: FiltroSemanalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg filtros: FiltroSemanalEntity)

    @Update
    suspend fun updateFiltro(filtro: FiltroSemanalEntity)

    @Query("SELECT * FROM FiltrosSemanales WHERE TRIM(LOWER(mes)) = TRIM(LOWER(:mes)) AND semana = :semana LIMIT 1")
    suspend fun obtenerFiltroPorSemana(mes: String, semana: String): FiltroSemanalEntity?

    @Query("SELECT COUNT(*) FROM FiltrosSemanales")
    fun count(): Int

    @Query("DELETE FROM FiltrosSemanales WHERE mes = :mes AND semana = :semana")
    suspend fun eliminarFiltroPorSemana(mes: String, semana: String)
}
