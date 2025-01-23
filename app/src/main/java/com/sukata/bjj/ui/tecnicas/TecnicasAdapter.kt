package com.sukata.bjj.ui.tecnicas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sukata.bjj.R
import com.sukata.bjj.data.entities.TecnicaEntity

class TecnicasAdapter(
    private val onItemClick: (TecnicaEntity) -> Unit
) : RecyclerView.Adapter<TecnicasAdapter.TecnicaViewHolder>() {

    private var tecnicas = listOf<TecnicaEntity>()

    fun submitList(newList: List<TecnicaEntity>) {

        tecnicas = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TecnicaViewHolder, position: Int) {
        val tecnica = tecnicas[position]

        holder.bind(tecnica)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TecnicaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tecnica, parent, false)
        return TecnicaViewHolder(view)
    }

    override fun getItemCount(): Int = tecnicas.size

    inner class TecnicaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreTecnica)
        private val tvTipo: TextView = itemView.findViewById(R.id.tvTipoTecnica)
        private val tvDificultad: TextView = itemView.findViewById(R.id.tvDificultadTecnica)

        fun bind(tecnica: TecnicaEntity) {
            tvNombre.text = tecnica.nombre
            tvTipo.text = "Tipo: ${tecnica.tipoTecnica}"
            tvDificultad.text = "Dificultad: ${tecnica.dificultad?.name}"

            itemView.setOnClickListener {
                onItemClick(tecnica)
            }
        }
    }
}
