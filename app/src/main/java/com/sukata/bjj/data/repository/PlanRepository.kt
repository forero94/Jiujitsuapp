package com.sukata.bjj.data.repository

import androidx.lifecycle.LiveData
import com.sukata.bjj.data.daos.PlanDao
import com.sukata.bjj.data.entities.Plan

class PlanRepository(private val planDao: PlanDao) {

    val allPlanes: LiveData<List<Plan>> = planDao.getAllPlanes()

    suspend fun insert(plan: Plan) {
        planDao.insertPlan(plan)
    }

    suspend fun delete(plan: Plan) {
        planDao.deletePlan(plan)
    }

    suspend fun update(plan: Plan) {
        planDao.updatePlan(plan)
    }
}
