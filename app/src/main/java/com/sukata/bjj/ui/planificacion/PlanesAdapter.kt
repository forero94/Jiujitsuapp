package com.sukata.bjj.ui.planificacion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sukata.bjj.data.entities.Plan
import com.sukata.bjj.databinding.ItemPlanBinding

class PlanesAdapter(
    private val onItemClick: (Plan) -> Unit,
    private val onDeleteClick: (Plan) -> Unit
) : ListAdapter<Plan, PlanesAdapter.PlanViewHolder>(PlanDiffCallback()) {

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
            binding.tvPlanName.text = plan.primeraPosicion

            // Manejar clic en el ítem (por ejemplo, para editar)
            binding.root.setOnClickListener {
                onItemClick(plan)
            }

            // Manejar clic en el botón de eliminar
            binding.btnDelete.setOnClickListener {
                onDeleteClick(plan)
            }
        }
    }

    class PlanDiffCallback : DiffUtil.ItemCallback<Plan>() {
        override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean =
            oldItem == newItem
    }
}
