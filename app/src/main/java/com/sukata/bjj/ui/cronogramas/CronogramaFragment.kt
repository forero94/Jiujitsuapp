package com.sukata.bjj.ui.cronogramas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sukata.bjj.R
import com.sukata.bjj.ui.tecnicas.TecnicasAdapter
import com.sukata.bjj.ui.tecnicas.TecnicasViewModel
import kotlinx.coroutines.launch
import com.sukata.bjj.data.db.obtenerMesYSemanaActual
import com.sukata.bjj.ui.tecnicas.DetalleTecnicaDialogFragment

class CronogramaFragment : Fragment() {
    private lateinit var cinturonBlanco: View
    private lateinit var cinturonAzul: View
    private lateinit var cinturonVioleta: View
    private lateinit var cinturonMarron: View
    private lateinit var cinturonNegro: View
    private lateinit var rectanguloBlanco: View
    private lateinit var rectanguloAzul: View
    private lateinit var rectanguloVioleta: View
    private lateinit var rectanguloMarron: View
    private lateinit var rectanguloNegro: View
    private var dificultadSeleccionada: String? = null
    private lateinit var progressBar: ProgressBar

    private val tecnicasViewModel: TecnicasViewModel by viewModels()
    private lateinit var tecnicaSemanaAdapter: TecnicasAdapter
    private lateinit var filtroInfoTextView: TextView  // TextView para mostrar filtros aplicados

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,

        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_cronograma, container, false)
        cargarTecnicasSemana()
        progressBar = view.findViewById(R.id.progressBar)
        filtroInfoTextView = view.findViewById(R.id.filtroInfoText)
        val verCronogramaButton: Button = view.findViewById(R.id.verCronogramaButton)
        val editarCronogramaButton: Button = view.findViewById(R.id.editarCronogramaButton)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewTecnicaSemana)

        // Configuración del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        tecnicaSemanaAdapter = TecnicasAdapter { tecnica ->
            DetalleTecnicaDialogFragment.newInstance(tecnica).show(parentFragmentManager, "DetalleTecnica")
        }

        recyclerView.adapter = tecnicaSemanaAdapter
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.setHasFixedSize(true)


        verCronogramaButton.setOnClickListener {
            val dialog = VerCronogramaDialogFragment()
            dialog.show(parentFragmentManager, "VerCronogramaDialog")
        }

        editarCronogramaButton.setOnClickListener {
            val dialog = EditarCronogramaDialogFragment()
            dialog.show(childFragmentManager, "EditarCronogramaDialog")
        }


        // Registrar listener para resultados con la clave "filtroUpdated"


        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        progressBar = view.findViewById(R.id.progressBar)
        cinturonBlanco = view.findViewById(R.id.cinturonBlanco)
        cinturonAzul = view.findViewById(R.id.cinturonAzul)
        cinturonVioleta = view.findViewById(R.id.cinturonVioleta)
        cinturonMarron = view.findViewById(R.id.cinturonMarron)
        cinturonNegro = view.findViewById(R.id.cinturonNegro)
        rectanguloBlanco = view.findViewById(R.id.rectanguloBlanco)
        rectanguloAzul = view.findViewById(R.id.rectanguloAzul)
        rectanguloVioleta = view.findViewById(R.id.rectanguloVioleta)
        rectanguloMarron = view.findViewById(R.id.rectanguloMarron)
        rectanguloNegro = view.findViewById(R.id.rectanguloNegro)

        // Configurar listeners
        cinturonBlanco.setOnClickListener { aplicarFiltro("BLANCO") }
        cinturonAzul.setOnClickListener { aplicarFiltro("AZUL") }
        cinturonVioleta.setOnClickListener { aplicarFiltro("VIOLETA") }
        cinturonMarron.setOnClickListener { aplicarFiltro("MARRON") }
        cinturonNegro.setOnClickListener { aplicarFiltro("NEGRO") }

        // Cargar datos después de que las vistas estén inicializadas
        cargarTecnicasSemana()
    }



    private fun aplicarFiltro(dificultad: String) {
        Log.d("CronogramaFragment", "Filtro aplicado: Dificultad=$dificultad")

        // Si la misma dificultad está seleccionada, deselecciónala
        if (dificultad == dificultadSeleccionada) {
            dificultadSeleccionada = null
            ocultarTodosLosRectangulos()
        } else {
            dificultadSeleccionada = dificultad
            val rectangulos = listOf(rectanguloBlanco, rectanguloAzul, rectanguloVioleta, rectanguloMarron, rectanguloNegro)
            rectangulos.forEach { it.visibility = View.GONE }

            when (dificultad) {
                "BLANCO" -> rectanguloBlanco.visibility = View.VISIBLE
                "AZUL" -> rectanguloAzul.visibility = View.VISIBLE
                "VIOLETA" -> rectanguloVioleta.visibility = View.VISIBLE
                "MARRON" -> rectanguloMarron.visibility = View.VISIBLE
                "NEGRO" -> rectanguloNegro.visibility = View.VISIBLE
            }
        }

        // Recargar técnicas con ambos filtros
        cargarTecnicasSemana()
    }


    private fun ocultarTodosLosRectangulos() {
        val rectangulos = listOf(rectanguloBlanco, rectanguloAzul, rectanguloVioleta, rectanguloMarron, rectanguloNegro)
        rectangulos.forEach { it.visibility = View.GONE }
    }



    private fun cargarTecnicasSemana() {
        val (mesActual, semanaActual) = obtenerMesYSemanaActual()
        Log.d("CronogramaFragment", "Consultando filtro para mes=$mesActual, semana=$semanaActual y dificultad=$dificultadSeleccionada")

        // Mostrar ProgressBar
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val filtroSemana = tecnicasViewModel.obtenerFiltroParaSemana(mesActual, semanaActual)

            // Obtener técnicas con ambos filtros: dificultad y semana
            val tecnicasFiltradas = tecnicasViewModel.obtenerTecnicasCombinadas(filtroSemana, dificultadSeleccionada)

            // Ocultar ProgressBar y mostrar los datos
            progressBar.visibility = View.GONE
            tecnicaSemanaAdapter.submitList(tecnicasFiltradas)

            // Actualizar la descripción del filtro
            val posicion = filtroSemana?.posicionInicial ?: "Todos"
            val tipo = filtroSemana?.tipoTecnica ?: "Todos"
            val dificultadTexto = dificultadSeleccionada ?: "Todas las dificultades"

            val filtrosTexto = buildString {
                if (tipo != "Todos") append("$tipo") else append("Cualquier técnica")
                append(" desde ")
                if (posicion != "Todos") append("$posicion") else append("cualquier posición")
            }
            filtroInfoTextView.text = filtrosTexto
        }
    }




}
