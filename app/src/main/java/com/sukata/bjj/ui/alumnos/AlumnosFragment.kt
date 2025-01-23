package com.sukata.bjj.ui.alumnos

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sukata.bjj.R
import com.sukata.bjj.data.db.AppDatabase
import com.sukata.bjj.data.db.ClasesPorSemana
import com.sukata.bjj.data.entities.AlumnoEntity
import com.sukata.bjj.databinding.FragmentAlumnosBinding
import java.util.Date
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels


class AlumnosFragment : Fragment() {

    private var _binding: FragmentAlumnosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AlumnosViewModel by activityViewModels {
        AlumnosViewModelFactory(AppDatabase.getDatabase(requireContext()).alumnoDao())
    }
    private lateinit var adapter: AlumnosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlumnosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView
        adapter = AlumnosAdapter(onItemClick = { alumno ->
            // Acción al hacer clic en un alumno
            mostrarDialogoEditarAlumno(alumno)
        })
        binding.recyclerViewAlumnos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewAlumnos.adapter = adapter
        binding.searchViewAlumnos.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setFiltro(nombre = query?.takeIf { it.isNotEmpty() })
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setFiltro(nombre = newText?.takeIf { it.isNotEmpty() })
                return true
            }
        })
        var estadoAbonado: Boolean? = null // Estado inicial (Todos)

        binding.btnFilterAbonado.setOnClickListener {
            estadoAbonado = when (estadoAbonado) {
                null -> true // Filtrar abonados
                true -> false // Filtrar no abonados
                false -> null // Mostrar todos
            }

            // Cambiar texto del botón según el estado
            binding.btnFilterAbonado.text = when (estadoAbonado) {
                true -> "Abonado: Sí"
                false -> "Abonado: No"
                null -> "Abonado: Todos"
            }

            viewModel.setFiltro(abonado = estadoAbonado)
        }


        // Configurar filtros desde la vista

        // Observa los alumnos filtrados y actualiza el RecyclerView
        viewModel.alumnosFiltrados.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }


        // Configurar botón para agregar alumnos
        binding.fabAddAlumno.setOnClickListener {
            mostrarDialogoAgregarAlumno()
        }
    }

    private fun mostrarDialogoAgregarAlumno() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_alumno, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Agregar Alumno")
            .create()

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreAlumno)
        val spinnerGrado = dialogView.findViewById<Spinner>(R.id.spinnerGradoAlumno)
        val etId = dialogView.findViewById<EditText>(R.id.etIdAlumno)
        val etEdad = dialogView.findViewById<EditText>(R.id.etEdadAlumno)
        val etCelular = dialogView.findViewById<EditText>(R.id.etCelularAlumno)

        val btnAgregar = dialogView.findViewById<Button>(R.id.btnAgregarAlumno)

        // Configurar el Spinner con los valores predefinidos
        val grados = listOf("BLANCO", "AZUL", "VIOLETA", "MARRON", "NEGRO")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, grados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrado.adapter = adapter

        btnAgregar.setOnClickListener {
            val id = etId.text.toString().trim()
            val nombre = etNombre.text.toString().trim()
            val cinturon = spinnerGrado.selectedItem.toString()
            val edad = etEdad.text.toString().trim().toIntOrNull() ?: 0
            val celular = etCelular.text.toString().trim()


            if (nombre.isNotEmpty() && cinturon.isNotEmpty()) {
                val nuevoAlumno = AlumnoEntity(
                    idAlumno = id,
                    nombre = nombre,
                    cinturon = cinturon,
                    abonado = false, // Valor predeterminado
                    celular = celular,
                    fechaNacimiento = null, // Valor inicial (puede ser modificado en el futuro)
                    fechaAfiliacion = Date(), // Fecha actual como valor inicial
                    clasesPorSemana = ClasesPorSemana.UNA // Valor predeterminado
                )
                viewModel.insert(nuevoAlumno)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Nombre y Grado son obligatorios.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun mostrarDialogoEditarAlumno(alumno: AlumnoEntity) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_alumno, null)

        // Construir el diálogo
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Editar Alumno")
            .create()

        // Referenciar las vistas
        val tvId = dialogView.findViewById<TextView>(R.id.tvIdEditAlumno)
        val tvIdAlumno = dialogView.findViewById<TextView>(R.id.tvIdAlumnoEdit)
        val tvNombre = dialogView.findViewById<TextView>(R.id.tvNombreEditAlumno)
        val tvCinturon = dialogView.findViewById<TextView>(R.id.tvCinturonEditAlumno)
        val tvEdad = dialogView.findViewById<TextView>(R.id.tvEdadEditAlumno)
        val tvCelular = dialogView.findViewById<TextView>(R.id.tvCelularEditAlumno)
        val cbAbonado = dialogView.findViewById<CheckBox>(R.id.cbAbonadoEditAlumno)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardarEditAlumno)

        // Configurar los datos del alumno
        tvId.text = "ID: ${alumno.id}"
        tvIdAlumno.text = "ID Alumno: ${alumno.idAlumno}"
        tvNombre.text = "Nombre: ${alumno.nombre}"
        tvCinturon.text = "Cinturón: ${alumno.cinturon ?: "N/A"}"
        tvEdad.text = "Edad: ${alumno.edad} años"
        tvCelular.text = "Celular: ${alumno.celular ?: "No disponible"}"
        cbAbonado.isChecked = alumno.abonado == true

        // Acción al guardar cambios
        btnGuardar.setOnClickListener {
            val nuevoEstadoAbonado = cbAbonado.isChecked

            // Crear una copia actualizada del alumno
            val alumnoActualizado = alumno.copy(abonado = nuevoEstadoAbonado)

            // Actualizar en la base de datos a través del ViewModel
            viewModel.updateAlumno(alumnoActualizado)

            dialog.dismiss()
        }

        dialog.show()
    }

}
