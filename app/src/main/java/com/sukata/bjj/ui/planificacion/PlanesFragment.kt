// PlanesFragment.kt
package com.sukata.bjj.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sukata.bjj.R
import com.sukata.bjj.data.entities.Plan
import com.sukata.bjj.databinding.DialogNuevoPlanBinding
import com.sukata.bjj.databinding.FragmentPlanesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sukata.bjj.ui.planificacion.PlanViewModel
import com.sukata.bjj.ui.planificacion.PlanViewModelFactory
import com.sukata.bjj.ui.planificacion.PlanesAdapter

class PlanesFragment : Fragment() {

    private var _binding: FragmentPlanesBinding? = null
    private val binding get() = _binding!!

    private val planViewModel: PlanViewModel by viewModels {
        PlanViewModelFactory(requireActivity().application)
    }

    private lateinit var adapter: PlanesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlanesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Configurar RecyclerView
        adapter = PlanesAdapter { plan ->
            // Manejar clic en el plan (opcional: editar o eliminar)
            mostrarDialogoEditarPlan(plan)
        }
        binding.recyclerViewPlanes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPlanes.adapter = adapter

        // Observar los cambios en la lista de planes
        planViewModel.allPlanes.observe(viewLifecycleOwner) { planes ->
            planes?.let {
                adapter.submitList(it)
            }
        }

        // Manejar clic en el FAB para agregar un nuevo plan
        binding.fabNuevoPlan.setOnClickListener {
            mostrarDialogoAgregarPlan()
        }
    }

    private fun mostrarDetallesPlan(plan: Plan) {
        // Opcional: Mostrar detalles, editar o eliminar el plan
        // Por simplicidad, mostraremos un Toast
        Toast.makeText(
            requireContext(),
            "Plan: ${plan.primeraPosicion}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun mostrarDialogoAgregarPlan() {
        // Inflar el diseño del diálogo usando View Binding
        val dialogBinding = DialogNuevoPlanBinding.inflate(LayoutInflater.from(context))

        // Obtener referencia al contenedor de campos dinámicos
        val containerCamposDinamicos = dialogBinding.llCamposDinamicos

        // Variable para rastrear el último tipo de campo agregado
        var ultimoCampoAccion = true // Inicia con acción inicial

        // Función para agregar un nuevo campo
        fun agregarCampo() {
            val inflater = LayoutInflater.from(context)
            val nuevoCampo: View

            if (ultimoCampoAccion) {
                // Agregar un campo de Reacción
                nuevoCampo = inflater.inflate(R.layout.item_reaccion, containerCamposDinamicos, false)
                ultimoCampoAccion = false
            } else {
                // Agregar un campo de Acción
                nuevoCampo = inflater.inflate(R.layout.item_accion, containerCamposDinamicos, false)
                ultimoCampoAccion = true
            }

            containerCamposDinamicos.addView(nuevoCampo)
        }

        // Crear el diálogo
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false) // Evita que se cierre al tocar fuera
            .create()

        // Manejar clic en el botón Guardar
        dialogBinding.btnGuardar.setOnClickListener {
            val accionInicial = dialogBinding.etAccionInicial.text.toString().trim()

            if (accionInicial.isEmpty()) {
                dialogBinding.tilAccionInicial.error = "Este campo es requerido"
                return@setOnClickListener
            } else {
                dialogBinding.tilAccionInicial.error = null
            }

            // Recopilar acciones y reacciones
            val acciones = mutableListOf<String>()
            val reacciones = mutableListOf<String>()

            // Acción Inicial
            acciones.add(accionInicial)

            // Iterar sobre los campos dinámicos
            for (i in 0 until containerCamposDinamicos.childCount) {
                val campo = containerCamposDinamicos.getChildAt(i)
                val tilAccion = campo.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilAccionDinamico)
                val tilReaccion = campo.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilReaccionDinamico)

                tilAccion?.let {
                    val accion = it.editText?.text.toString().trim()
                    if (accion.isEmpty()) {
                        it.error = "Este campo es requerido"
                        return@setOnClickListener
                    } else {
                        it.error = null
                        acciones.add(accion)
                    }
                }

                tilReaccion?.let {
                    val reaccion = it.editText?.text.toString().trim()
                    if (reaccion.isEmpty()) {
                        it.error = "Este campo es requerido"
                        return@setOnClickListener
                    } else {
                        it.error = null
                        reacciones.add(reaccion)
                    }
                }
            }

            // Crear una nueva instancia de Plan
            val nuevoPlan = Plan(
                primeraPosicion = accionInicial,
                acciones = acciones,
                reacciones = reacciones
            )

            // Insertar el nuevo plan a través del ViewModel
            planViewModel.insert(nuevoPlan)

            // Mostrar mensaje de confirmación
            Toast.makeText(requireContext(), "Nuevo plan agregado", Toast.LENGTH_SHORT).show()

            // Cerrar el diálogo
            dialog.dismiss()
        }

        // Manejar clic en el botón Cerrar
        dialogBinding.btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        // Manejar clic en el FAB para agregar un nuevo paso
        dialogBinding.fabNuevoPaso.setOnClickListener {
            agregarCampo()
        }

        // Mostrar el diálogo
        dialog.show()
    }

    private fun mostrarDialogoEditarPlan(plan: Plan) {
        // Inflar el diseño del diálogo usando View Binding
        val dialogBinding = DialogNuevoPlanBinding.inflate(LayoutInflater.from(context))

        // Obtener referencia al contenedor de campos dinámicos
        val containerCamposDinamicos = dialogBinding.llCamposDinamicos

        // Variable para rastrear el último tipo de campo agregado
        var ultimoCampoAccion = true // Asume que la primera acción es "Acción"

        // Función para agregar un nuevo campo
        fun agregarCampo(tipo: String, texto: String = "") {
            val inflater = LayoutInflater.from(context)
            val nuevoCampo: View

            if (tipo == "reaccion") {
                nuevoCampo = inflater.inflate(R.layout.item_reaccion, containerCamposDinamicos, false)
                ultimoCampoAccion = false
                // Prellenar el campo
                val etReaccion = nuevoCampo.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etReaccionDinamico)
                etReaccion.setText(texto)
            } else {
                nuevoCampo = inflater.inflate(R.layout.item_accion, containerCamposDinamicos, false)
                ultimoCampoAccion = true
                // Prellenar el campo
                val etAccion = nuevoCampo.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etAccionDinamico)
                etAccion.setText(texto)
            }

            containerCamposDinamicos.addView(nuevoCampo)
        }

        // Prellenar los campos con los datos del plan
        dialogBinding.etAccionInicial.setText(plan.primeraPosicion)

        // Supongamos que `acciones` y `reacciones` están en orden alternado
        val acciones = plan.acciones
        val reacciones = plan.reacciones
        val maxSize = maxOf(acciones.size, reacciones.size)

        for (i in 0 until maxSize) {
            if (i < acciones.size) {
                agregarCampo("accion", acciones[i])
            }
            if (i < reacciones.size) {
                agregarCampo("reaccion", reacciones[i])
            }
        }

        // Crear el diálogo
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false) // Evita que se cierre al tocar fuera
            .create()

        // Manejar clic en el botón Guardar (Actualizar)
        dialogBinding.btnGuardar.setOnClickListener {
            val accionInicial = dialogBinding.etAccionInicial.text.toString().trim()

            if (accionInicial.isEmpty()) {
                dialogBinding.tilAccionInicial.error = "Este campo es requerido"
                return@setOnClickListener
            } else {
                dialogBinding.tilAccionInicial.error = null
            }

            // Recopilar acciones y reacciones
            val nuevasAcciones = mutableListOf<String>()
            val nuevasReacciones = mutableListOf<String>()

            // Acción Inicial
            nuevasAcciones.add(accionInicial)

            // Iterar sobre los campos dinámicos
            for (i in 0 until containerCamposDinamicos.childCount) {
                val campo = containerCamposDinamicos.getChildAt(i)
                val tilAccion = campo.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilAccionDinamico)
                val tilReaccion = campo.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilReaccionDinamico)

                tilAccion?.let {
                    val accion = it.editText?.text.toString().trim()
                    if (accion.isEmpty()) {
                        it.error = "Este campo es requerido"
                        return@setOnClickListener
                    } else {
                        it.error = null
                        nuevasAcciones.add(accion)
                    }
                }

                tilReaccion?.let {
                    val reaccion = it.editText?.text.toString().trim()
                    if (reaccion.isEmpty()) {
                        it.error = "Este campo es requerido"
                        return@setOnClickListener
                    } else {
                        it.error = null
                        nuevasReacciones.add(reaccion)
                    }
                }
            }

            // Crear una instancia actualizada de Plan
            val planActualizado = plan.copy(
                primeraPosicion = accionInicial,
                acciones = nuevasAcciones,
                reacciones = nuevasReacciones
            )

            // Actualizar el plan a través del ViewModel
            planViewModel.update(planActualizado)

            // Mostrar mensaje de confirmación
            Toast.makeText(requireContext(), "Plan actualizado", Toast.LENGTH_SHORT).show()

            // Cerrar el diálogo
            dialog.dismiss()
        }

        // Manejar clic en el botón Cerrar
        dialogBinding.btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        // Manejar clic en el FAB para agregar un nuevo paso
        dialogBinding.fabNuevoPaso.setOnClickListener {
            // Determinar el tipo de campo a agregar basado en el último agregado
            val tipoCampo = if (ultimoCampoAccion) "reaccion" else "accion"
            agregarCampo(tipoCampo)
        }

        // Mostrar el diálogo
        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
