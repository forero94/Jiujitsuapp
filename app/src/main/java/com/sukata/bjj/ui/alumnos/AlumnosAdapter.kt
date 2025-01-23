package com.sukata.bjj.ui.alumnos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sukata.bjj.R
import com.sukata.bjj.data.entities.AlumnoEntity

class AlumnosAdapter(
    private val onItemClick: (AlumnoEntity) -> Unit
) : RecyclerView.Adapter<AlumnosAdapter.AlumnoViewHolder>() {

    private var alumnos = listOf<AlumnoEntity>()

    fun submitList(newList: List<AlumnoEntity>) {
        alumnos = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = alumnos[position]
        holder.bind(alumno)
    }

    override fun getItemCount(): Int = alumnos.size

    inner class AlumnoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreAlumno)
        private val tvGrado: TextView = itemView.findViewById(R.id.tvGradoAlumno)
        private val indicatorAbonado: View = itemView.findViewById(R.id.indicatorAbonado)

        fun bind(alumno: AlumnoEntity) {
            tvNombre.text = alumno.nombre
            tvGrado.text = "Grado: ${alumno.cinturon}"
            val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.indicator_circle)

            indicatorAbonado.background = drawable

            val color = if (alumno.abonado == true) {
                itemView.context.getColor(R.color.green) // Verde
            } else {
                itemView.context.getColor(R.color.red) // Rojo
            }
            drawable?.setTint(color) // Aplica el color dinámicamente
            itemView.setOnClickListener {
                onItemClick(alumno)
            }
        }
    }
}
