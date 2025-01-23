package com.sukata.bjj.ui.cronogramas

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import android.widget.ExpandableListView
import com.sukata.bjj.R

class EditarCronogramaDialogFragment : DialogFragment() {

    // Variable para rastrear el grupo expandido actual
    private var expandedGroupPosition: Int = -1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflar el layout personalizado
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_editar_cronograma, null)

        // Obtener referencia al ExpandableListView
        val expandableListView: ExpandableListView = dialogView.findViewById(R.id.expandableListViewCronograma)

        // Preparar datos para el ExpandableListView
        val listDataHeader = obtenerMeses()                // Lista de meses
        val listDataChild = obtenerSemanasPorMes(listDataHeader)  // Map de mes -> lista de semanas

        // Configurar el adaptador personalizado
        val listAdapter = CronogramaExpandableListAdapter(requireContext(), listDataHeader, listDataChild)
        expandableListView.setAdapter(listAdapter)

        // Configurar el listener para gestionar la expansión
        expandableListView.setOnGroupExpandListener { groupPosition ->
            // Colapsa el grupo previamente expandido, si existe
            if (expandedGroupPosition != -1 && groupPosition != expandedGroupPosition) {
                expandableListView.collapseGroup(expandedGroupPosition)
            }
            // Actualiza el índice del grupo expandido actual
            expandedGroupPosition = groupPosition
        }

        // Listener para actualizar el índice cuando un grupo se colapsa
        expandableListView.setOnGroupCollapseListener { groupPosition ->
            if (groupPosition == expandedGroupPosition) {
                expandedGroupPosition = -1
            }
        }

        // Listener para manejar clics en los hijos (semana)
        expandableListView.setOnChildClickListener { parent, view, groupPosition, childPosition, id ->
            val mes = listDataHeader[groupPosition]
            val semana = listDataChild[mes]?.get(childPosition) ?: ""

            // Crear y mostrar el diálogo para añadir técnicas
            val addTechDialog = AnadirTecnicaDialogFragment.newInstance(mes, semana)
            addTechDialog.show(parentFragmentManager, "AddTechDialog")
            true
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Editar Cronograma")
            .setView(dialogView)
            .setNegativeButton("Cerrar") { dialog, _ ->
                val result = Bundle().apply {
                    putBoolean("actualizar", true)
                }
                parentFragmentManager.setFragmentResult("filtroUpdated", result)
                dismiss()
                dialog.dismiss()
            }
            .create()
    }

    // Función para obtener una lista de meses (ejemplo)
    private fun obtenerMeses(): List<String> {
        return listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
    }

    // Función para obtener semanas para cada mes (datos de ejemplo)
    private fun obtenerSemanasPorMes(meses: List<String>): HashMap<String, List<String>> {
        val semanasPorMes = HashMap<String, List<String>>()
        for (mes in meses) {
            // Suponiendo 4 semanas por mes para fines de ejemplo
            semanasPorMes[mes] = listOf("Semana 1", "Semana 2", "Semana 3", "Semana 4")
        }
        return semanasPorMes
    }
}
