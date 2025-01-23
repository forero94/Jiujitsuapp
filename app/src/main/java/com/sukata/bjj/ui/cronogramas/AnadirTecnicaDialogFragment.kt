package com.sukata.bjj.ui.cronogramas

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sukata.bjj.R
import com.sukata.bjj.data.entities.FiltroSemanalEntity
import com.sukata.bjj.data.entities.TecnicaEntity
import com.sukata.bjj.ui.tecnicas.TecnicasAdapter
import com.sukata.bjj.ui.tecnicas.TecnicasViewModel
import kotlinx.coroutines.launch

class AnadirTecnicaDialogFragment : DialogFragment() {
    private var mesActual: String = ""
    private var semanaActual: String = ""
    companion object {
        fun newInstance(mes: String, semana: String): AnadirTecnicaDialogFragment {
            val fragment = AnadirTecnicaDialogFragment()
            val args = Bundle()
            args.putString("mes", mes)
            args.putString("semana", semana)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var tecnicasAdapter: TecnicasAdapter
    private lateinit var tecnicasViewModel: TecnicasViewModel
    private var listaCompletaTecnicas: List<TecnicaEntity> = emptyList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Asignar valores de argumentos a mesActual y semanaActual
        mesActual = arguments?.getString("mes") ?: ""
        semanaActual = arguments?.getString("semana") ?: ""

        tecnicasViewModel = ViewModelProvider(this).get(TecnicasViewModel::class.java)

        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_anadir_tecnica_cronograma, null)

        val spinnerPosicion = dialogView.findViewById<Spinner>(R.id.spinnerPosicionInicialFiltro)
        val spinnerTipo = dialogView.findViewById<Spinner>(R.id.spinnerTipoTecnicaFiltro)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.tecnicasRecyclerViewFiltro)

        recyclerView.layoutManager = LinearLayoutManager(context)
        tecnicasAdapter = TecnicasAdapter { tecnica ->
            // Acción al hacer clic en una técnica, si se necesita.
        }
        recyclerView.adapter = tecnicasAdapter

        // Definir listeners de spinners sin asignarlos aún
        val listenerPosicion = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val posSeleccionada = spinnerPosicion.selectedItem?.toString() ?: "Todos"
                val tipoSeleccionado = spinnerTipo.selectedItem?.toString() ?: "Todos"
                aplicarFiltros(posSeleccionada, tipoSeleccionado)

                lifecycleScope.launch {
                    val filtro = FiltroSemanalEntity(
                        mes = mesActual.lowercase().trim(), // Convertir a minúsculas y eliminar espacios innecesarios
                        semana = semanaActual.replace("Semana ", "").trim(), // Eliminar "Semana " y espacios innecesarios
                        posicionInicial = spinnerPosicion.selectedItem?.toString()?.takeIf { it != "Todos" },
                        tipoTecnica = spinnerTipo.selectedItem?.toString()?.takeIf { it != "Todos" }
                    )
                    tecnicasViewModel.guardarFiltroSemanal(filtro)
                    Log.d("AnadirTecnicaDialog", "Filtro guardado con valores normalizados: $filtro")

                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val listenerTipo = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val posSeleccionada = spinnerPosicion.selectedItem?.toString() ?: "Todos"
                val tipoSeleccionado = spinnerTipo.selectedItem?.toString() ?: "Todos"
                aplicarFiltros(posSeleccionada, tipoSeleccionado)

                lifecycleScope.launch {
                    val filtro = FiltroSemanalEntity(
                        mes = mesActual.lowercase().trim(), // Convertir a minúsculas y eliminar espacios innecesarios
                        semana = semanaActual.replace("Semana ", "").trim(), // Eliminar "Semana " y espacios innecesarios
                        posicionInicial = spinnerPosicion.selectedItem?.toString()?.takeIf { it != "Todos" },
                        tipoTecnica = spinnerTipo.selectedItem?.toString()?.takeIf { it != "Todos" }
                    )
                    tecnicasViewModel.guardarFiltroSemanal(filtro)
                    Log.d("AnadirTecnicaDialog", "Filtro guardado con valores normalizados: $filtro")

                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        lifecycleScope.launch {
            // Configurar adaptadores para spinners
            val posicionesIniciales = listOf("Todos") + tecnicasViewModel.posicionesIniciales.value.orEmpty()
            val tiposTecnica = listOf("Todos") + tecnicasViewModel.tiposTecnica.value.orEmpty()

            spinnerPosicion.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, posicionesIniciales)
            spinnerTipo.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tiposTecnica)

            // Cargar filtro desde la base de datos para la semana actual
            val filtroExistente = tecnicasViewModel.obtenerFiltroParaSemana(mesActual, semanaActual)
            filtroExistente?.let { filtro ->
                // Desactivar listeners temporalmente
                spinnerPosicion.onItemSelectedListener = null
                spinnerTipo.onItemSelectedListener = null

                spinnerPosicion.setSelection(
                    (spinnerPosicion.adapter as ArrayAdapter<String>).getPosition(filtro.posicionInicial ?: "Todos")
                )
                spinnerTipo.setSelection(
                    (spinnerTipo.adapter as ArrayAdapter<String>).getPosition(filtro.tipoTecnica ?: "Todos")
                )

                // Reactivar listeners después de restaurar selecciones
                spinnerPosicion.onItemSelectedListener = listenerPosicion
                spinnerTipo.onItemSelectedListener = listenerTipo
            } ?: run {
                // Si no hay filtro existente, asignar listeners directamente
                spinnerPosicion.onItemSelectedListener = listenerPosicion
                spinnerTipo.onItemSelectedListener = listenerTipo
            }

            // Cargar todas las técnicas y aplicar filtros iniciales
            listaCompletaTecnicas = tecnicasViewModel.obtenerTodasTecnicas()
            aplicarFiltros(
                spinnerPosicion.selectedItem?.toString() ?: "Todos",
                spinnerTipo.selectedItem?.toString() ?: "Todos"
            )
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Añadir Técnicas para $semanaActual del mes $mesActual")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val filtro = FiltroSemanalEntity(
                    mes = mesActual,
                    semana = semanaActual,
                    posicionInicial = spinnerPosicion.selectedItem?.toString()?.takeIf { it != "Todos" },
                    tipoTecnica = spinnerTipo.selectedItem?.toString()?.takeIf { it != "Todos" }
                )
                lifecycleScope.launch {
                    tecnicasViewModel.guardarFiltroSemanal(filtro)
                    Log.d("EditarCronogramaDialog", "Filtro guardado: $filtro")
                }

                val result = Bundle().apply {
                    putBoolean("actualizar", true)
                }
                Log.d("EditarCronogramaDialog", "Enviando resultado: actualizar=true")
                parentFragmentManager.setFragmentResult("filtroUpdated", result)
                dialog.dismiss()
            }

            .create()
    }

    private fun aplicarFiltros(posicion: String, tipo: String) {
        val listaFiltrada = listaCompletaTecnicas.filter { tecnica ->
            val coincidePosicion = (posicion == "Todos") || (tecnica.posicionInicial == posicion)
            val coincideTipo = (tipo == "Todos") || (tecnica.tipoTecnica == tipo)
            coincidePosicion && coincideTipo
        }
        tecnicasAdapter.submitList(listaFiltrada)
        childFragmentManager.setFragmentResult("filtroUpdated", Bundle())

    }
}
