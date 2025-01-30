package com.sukata.bjj.ui.planificacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sukata.bjj.data.entities.Plan
import com.sukata.bjj.databinding.ItemPlanBinding

class PlanesAdapter(private val onItemClick: (Plan) -> Unit) :
    ListAdapter<Plan, PlanesAdapter.PlanViewHolder>(PlanDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val currentPlan = getItem(position)
        holder.bind(currentPlan)
    }

    inner class PlanViewHolder(private val binding: ItemPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: Plan) {
            binding.primeraPosicion.text = plan.primeraPosicion
            // Configura otros campos según tu diseño

            binding.root.setOnClickListener {
                onItemClick(plan)
            }
        }
    }

    class PlanDiffCallback : DiffUtil.ItemCallback<Plan>() {
        override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem.id == newItem.id // Asegúrate de que el ID sea único
        }

        override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem == newItem
        }
    }
}
