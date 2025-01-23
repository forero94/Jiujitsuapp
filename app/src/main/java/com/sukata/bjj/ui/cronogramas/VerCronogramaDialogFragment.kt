package com.sukata.bjj.ui.cronogramas

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ExpandableListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sukata.bjj.R
import com.sukata.bjj.ui.tecnicas.TecnicasViewModel
import kotlinx.coroutines.launch

class VerCronogramaDialogFragment : DialogFragment() {

    private val tecnicasViewModel: TecnicasViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflar el layout personalizado
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_ver_cronograma, null)

        // Configuración del ExpandableListView
        val expandableListView: ExpandableListView = dialogView.findViewById(R.id.expandableListViewVerCronograma)
        val listDataHeader = obtenerMeses()
        val listDataChild = hashMapOf<String, List<String>>() // Semana -> "Técnica - Posición"

        // Cargar datos y configurar adaptador
        cargarTecnicasSemana(listDataHeader, listDataChild) {
            val listAdapter = CronogramaExpandableListAdapter(requireContext(), listDataHeader, listDataChild)
            expandableListView.setAdapter(listAdapter)
        }

        // Configuración del listener para expandir y colapsar grupos
        var expandedGroupPosition = -1
        expandableListView.setOnGroupExpandListener { groupPosition ->
            if (expandedGroupPosition != -1 && groupPosition != expandedGroupPosition) {
                expandableListView.collapseGroup(expandedGroupPosition)
            }
            expandedGroupPosition = groupPosition
        }

        expandableListView.setOnGroupCollapseListener { groupPosition ->
            if (groupPosition == expandedGroupPosition) {
                expandedGroupPosition = -1
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Cronograma Completo")
            .setView(dialogView)
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    private fun cargarTecnicasSemana(
        listDataHeader: List<String>,
        listDataChild: HashMap<String, List<String>>,
        onComplete: () -> Unit
    ) {
        lifecycleScope.launch {
            listDataHeader.forEach { mes ->
                val semanas = obtenerSemanasPorMes(mes)
                val semanasConTecnicas = semanas.map { semana ->
                    val filtro = tecnicasViewModel.obtenerFiltroParaSemana(mes, semana)
                    val tecnica = filtro?.tipoTecnica ?: "Sin técnica"
                    val posicion = filtro?.posicionInicial ?: "Sin posición"
                    "$semana: $tecnica - $posicion" // Formato concatenado
                }
                listDataChild[mes] = semanasConTecnicas
            }
            onComplete()
        }
    }

    private fun obtenerMeses(): List<String> {
        return listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
    }

    private fun obtenerSemanasPorMes(mes: String): List<String> {
        return listOf("Semana 1", "Semana 2", "Semana 3", "Semana 4") // Simulación
    }
}
