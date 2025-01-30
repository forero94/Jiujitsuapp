package com.sukata.bjj.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sukata.bjj.data.entities.Plan

@Dao
interface PlanDao {

    @Query("SELECT * FROM planes ORDER BY id DESC")
    fun getAllPlanes(): LiveData<List<Plan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: Plan)

    @Delete
    suspend fun deletePlan(plan: Plan)

    @Update
    suspend fun updatePlan(plan: Plan)
}
