package com.sukata.bjj.ui.tecnicas

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sukata.bjj.R
import com.sukata.bjj.data.entities.Dificultad
import com.sukata.bjj.data.entities.TecnicaEntity
import com.sukata.bjj.databinding.FragmentTecnicasBinding

class TecnicasFragment : Fragment() {

    private var _binding: FragmentTecnicasBinding? = null
    private val binding get() = _binding!!

    // El ViewModel que maneja la lógica de filtrado y observación
    private val viewModel: TecnicasViewModel by viewModels()
    private lateinit var adapter: TecnicasAdapter

    //----------------------------------------------------------------------------------------------
    // CICLO DE VIDA DEL FRAGMENT
    //----------------------------------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTecnicasBinding.inflate(inflater, container, false)
        return binding.root
    }

    //----------------------------------------------------------------------------------------------
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuramos el Adaptador y el RecyclerView
        adapter = TecnicasAdapter(onItemClick = { tecnica ->
            DetalleTecnicaDialogFragment.newInstance(tecnica).show(parentFragmentManager, "DetalleTecnica")
        })

        binding.recyclerViewTecnicas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTecnicas.adapter = adapter

        // Observamos las técnicas filtradas y actualizamos la lista al cambiar
        viewModel.tecnicasFiltradas.observe(viewLifecycleOwner, Observer { lista ->
            adapter.submitList(lista)
            Log.d("TecnicasFragment", "Técnicas actualizadas: $lista")
        })


        // Configurar el botón flotante para agregar nuevas técnicas
        binding.fabAddTecnica.setOnClickListener {
            mostrarDialogoAgregarTecnica()
        }

        // Configurar el botón flotante para mostrar el diálogo de filtros
        binding.fabFilterTecnica.setOnClickListener {
            mostrarDialogoFiltros()
        }

        // Ocultamos el SearchView (ya no se filtra por nombre)
        configurarSearchView()
    }
    // Método para configurar el SearchView
    private fun configurarSearchView() {
        val searchView = binding.searchView
        searchView.setIconifiedByDefault(false) // Mostrar el SearchView expandido por defecto
        searchView.isSubmitButtonEnabled = false // Deshabilitar el botón de submit

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // No se necesita acción específica al enviar la consulta
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("SearchView", "Texto de búsqueda cambiado: $newText")
                // Actualizar el filtro de búsqueda en el ViewModel
                viewModel.setFiltros(textoBusqueda = if (!TextUtils.isEmpty(newText)) newText else null)
                return true
            }
        })
    }

    //----------------------------------------------------------------------------------------------
    // MÉTODO PARA MOSTRAR EL DIÁLOGO DE AGREGAR UNA NUEVA TÉCNICA
    //----------------------------------------------------------------------------------------------
    private fun mostrarDialogoAgregarTecnica() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_tecnica, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Agregar Técnica")
            .create()

        // Referencias de los campos existentes
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreTecnica)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcionTecnica)
        val spTipoTecnica = dialogView.findViewById<Spinner>(R.id.spinnerTipoTecnica)
        val spDificultad = dialogView.findViewById<Spinner>(R.id.spinnerDificultad)
        val spPosicionInicial = dialogView.findViewById<Spinner>(R.id.spinnerPosicionInicial)
        val etContexto = dialogView.findViewById<EditText>(R.id.etContexto)
        val etObjetivoPrincipal = dialogView.findViewById<EditText>(R.id.etObjetivoPrincipal)
        val spSubtipoPosicion = dialogView.findViewById<Spinner>(R.id.spSubtipoPosicion)
        // Referencias para fuentes
        val llFuentes = dialogView.findViewById<LinearLayout>(R.id.llFuentes)
        val btnAgregarFuente = dialogView.findViewById<Button>(R.id.btnAgregarFuente)

        // Referencia al botón para agregar la técnica
        val btnAgregar = dialogView.findViewById<Button>(R.id.btnAgregarTecnica)
        val posicionInicialSeleccionada = spPosicionInicial.selectedItem?.toString() ?: "Todos"
        if (posicionInicialSeleccionada != "Todos") {
            viewModel.cargarSubtiposPosicion(posicionInicialSeleccionada)
        }
        // Configuración inicial del Spinner de subtipo
        spSubtipoPosicion.visibility = View.GONE
        spSubtipoPosicion.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Todos")
        )

        viewModel.subtiposPosicionInicial.observe(viewLifecycleOwner, Observer { listaSubtipos ->
            if (listaSubtipos.isNotEmpty()) {
                spSubtipoPosicion.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf("Todos") + listaSubtipos
                )
                spSubtipoPosicion.visibility = View.VISIBLE
            } else {
                spSubtipoPosicion.visibility = View.GONE
            }
        })

        // Configuración inicial de los Spinners
        val tiposTecnica = arrayOf("Pasaje", "Inversion", "Finalizacion", "Progresion", "Escape", "Derribos", "Barridos", "Controles", "Re-Guard", "Defensa de Sumisiones", "Contraataques", "Respuestas")
        spTipoTecnica.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            tiposTecnica
        )

        val dificultades = Dificultad.values().map { it.name }
        spDificultad.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            dificultades
        )

        val posicionesIniciales = arrayOf(
            "De pie (Standing)",
            "Guardia cerrada (Closed Guard)",
            "Guardia abierta (Open Guard)",
            "Half Guard (Media Guardia)",
            "Butterfly Guard (Guardia Mariposa)",
            "X-Guard",
            "Side Control (Control Lateral)",
            "North-South",
            "Montada (Mount)",
            "Back Control (Control de espalda)",
            "Reverse Half Guard (Media guardia invertida)",
            "Turtle (Tortuga)",
            "Quarter Guard"
        )
        Log.d("AgregarTecnica", "Tipos de técnica: ${tiposTecnica.toList()}")
        Log.d("AgregarTecnica", "Posiciones iniciales: ${posicionesIniciales.toList()}")

        spPosicionInicial.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            posicionesIniciales
        )

        // Lógica para agregar dinámicamente campos de fuentes
        btnAgregarFuente.setOnClickListener {
            val nuevaFuente = EditText(requireContext()).apply {
                hint = "Ingrese una fuente"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            llFuentes.addView(nuevaFuente)
        }
        spPosicionInicial.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val posicionSeleccionada = spPosicionInicial.selectedItem?.toString() ?: "Todos"
                if (posicionSeleccionada != "Todos") {
                    viewModel.cargarSubtiposPosicion(posicionSeleccionada)
                } else {
                    spSubtipoPosicion.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // Acción al pulsar "Agregar Técnica"
        btnAgregar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val tipoTecnicaSel = spTipoTecnica.selectedItem.toString()
            val dificultadSel = spDificultad.selectedItem.toString()
            val posicionInicialSel = spPosicionInicial.selectedItem.toString()
            val subtipoPosicionSel = spSubtipoPosicion.selectedItem?.toString()
            if (spSubtipoPosicion.visibility == View.VISIBLE && (subtipoPosicionSel.isNullOrEmpty() || subtipoPosicionSel == "Todos")) {
                Toast.makeText(requireContext(), "Por favor, seleccione un subtipo válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (subtipoPosicionSel.isNullOrEmpty() || subtipoPosicionSel == "Todos") {
                Toast.makeText(requireContext(), "Por favor, seleccione un subtipo válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contexto = etContexto.text.toString().trim()
            val objetivoPrincipal = etObjetivoPrincipal.text.toString().trim()

            // Recolectar fuentes dinámicamente, ignorando campos vacíos
            val fuentes = mutableListOf<String>()
            for (i in 0 until llFuentes.childCount) {
                val fuenteView = llFuentes.getChildAt(i) as? EditText
                val fuenteTexto = fuenteView?.text.toString().trim()
                if (fuenteTexto.isNotEmpty()) { // Verificar que el campo no esté vacío
                    fuentes.add(fuenteTexto)
                }
            }
            val posicionInicialSeleccionada = spPosicionInicial.selectedItem?.toString() ?: "Todos"
            if (posicionInicialSeleccionada != "Todos") {
                viewModel.cargarSubtiposPosicion(posicionInicialSeleccionada)
            }




            // Validar campos
            if (nombre.isNotEmpty() && descripcion.isNotEmpty()) {
                val dificultadEnum = try {
                    Dificultad.valueOf(dificultadSel)
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(requireContext(), "Dificultad inválida.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Crear el objeto Técnica
                val nuevaTecnica = TecnicaEntity(
                    nombre = nombre,
                    posicionInicial = posicionInicialSel,
                    tipoTecnica = tipoTecnicaSel,
                    dificultad = dificultadEnum,
                    contexto = if (contexto.isNotEmpty()) contexto else null,
                    objetivoPrincipal = if (objetivoPrincipal.isNotEmpty()) objetivoPrincipal else null,
                    descripcion = descripcion,
                    variante = null,
                    recursosAdicionales = if (fuentes.isNotEmpty()) fuentes else null
                )
                Log.d("DebugFuentes", "Lista final de fuentes: $fuentes")

                // Insertar en la base de datos
                viewModel.insert(nuevaTecnica)

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Por favor, complete todos los campos requeridos.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }




    //----------------------------------------------------------------------------------------------
    // MÉTODO PARA MOSTRAR EL DIÁLOGO DE FILTROS (SIN FILTRAR POR NOMBRE)
    //----------------------------------------------------------------------------------------------
    private fun mostrarDialogoFiltros() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_filter_tecnicas, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Filtrar Técnicas")
            .create()

        // Referencias a los Spinners
        val spPosicionInicial = dialogView.findViewById<Spinner>(R.id.spPosicionInicial)
        val spSubtipoPosicionInicial = dialogView.findViewById<Spinner>(R.id.spSubtipoPosicionInicial)
        val tvSubtipoPosicionInicial = dialogView.findViewById<TextView>(R.id.tvSubtipoPosicionInicial)
        val spTipoTecnica = dialogView.findViewById<Spinner>(R.id.spTipoTecnica)
        val spDificultad = dialogView.findViewById<Spinner>(R.id.spDificultad)


        val btnAplicarFiltros = dialogView.findViewById<Button>(R.id.btnAplicarFiltros)





        viewModel.tiposTecnica.observe(viewLifecycleOwner, Observer { tipos ->
            val tiposOpciones = listOf("Todos") + tipos
            spTipoTecnica.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                tiposOpciones
            )
            Log.d("Fragment", "Tipos cargados: $tiposOpciones")
        })

        viewModel.dificultades.observe(viewLifecycleOwner, Observer { dificultades ->
            val dificultadesOpciones = listOf("Todos") + dificultades.map { it.name }
            spDificultad.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                dificultadesOpciones
            )
            Log.d("Fragment", "Dificultades cargadas: $dificultadesOpciones")
        })

        // Observa las opciones del ViewModel y actualiza los adaptadores
        viewModel.posicionesIniciales.observe(viewLifecycleOwner, Observer { opciones ->
            val posicionesIniciales = listOf("Todos") + opciones
            spPosicionInicial.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                posicionesIniciales
            )
        })

        viewModel.subtiposPosicionInicial.observe(viewLifecycleOwner, Observer { subtipos ->
            if (subtipos.isNotEmpty()) {
                tvSubtipoPosicionInicial.visibility = View.VISIBLE
                spSubtipoPosicionInicial.visibility = View.VISIBLE
                spSubtipoPosicionInicial.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf("Todos") + subtipos
                )
            } else {
                tvSubtipoPosicionInicial.visibility = View.GONE
                spSubtipoPosicionInicial.visibility = View.GONE
            }
        })

        // Cambiar subtipo cuando se selecciona posición inicial
        spPosicionInicial.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val posicionSeleccionada = spPosicionInicial.selectedItem.toString()
                if (posicionSeleccionada != "Todos") {
                    viewModel.cargarSubtiposPosicion(posicionSeleccionada)
                } else {
                    tvSubtipoPosicionInicial.visibility = View.GONE
                    spSubtipoPosicionInicial.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Acción al pulsar "Aplicar Filtros"
        btnAplicarFiltros.setOnClickListener {
            val posSel = spPosicionInicial.selectedItem?.toString() ?: "Todos"
            val subtipoSel = spSubtipoPosicionInicial.selectedItem?.toString() ?: "Todos"
            val tipoSel = spTipoTecnica.selectedItem?.toString() ?: "Todos"
            val difSel = spDificultad.selectedItem?.toString() ?: "Todos"

            // Convertir "Todos" a null para evitar filtros innecesarios
            val posicionInicial = if (posSel != "Todos") posSel else null
            val subtipoPosicionInicial = if (subtipoSel != "Todos") subtipoSel else null
            val tipoTecnica = if (tipoSel != "Todos") tipoSel else null
            val dificultad = if (difSel != "Todos") {
                try {
                    Dificultad.valueOf(difSel)
                } catch (e: IllegalArgumentException) {
                    null
                }
            } else null

            Log.d("FiltrosSeleccionados", "Posición: $posicionInicial, Subtipo: $subtipoPosicionInicial, " +
                    "Tipo: $tipoTecnica, Dificultad: $dificultad")

            viewModel.setFiltros(
                posicionInicial = posicionInicial,
                subtipoPosicionInicial = subtipoPosicionInicial,
                tipoTecnica = tipoTecnica,
                dificultad = dificultad
            )

            dialog.dismiss()
        }




        dialog.show()
    }



    //----------------------------------------------------------------------------------------------
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
