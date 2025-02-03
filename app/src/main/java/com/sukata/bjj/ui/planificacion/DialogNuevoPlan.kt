package com.sukata.bjj.ui.planificacion

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sukata.bjj.data.entities.Plan
import com.sukata.bjj.databinding.DialogNuevoPlanBinding

/**
 * Diálogo reutilizable para agregar o editar un Plan.
 *
 * @param context El contexto donde se mostrará el diálogo.
 * @param plan    Objeto Plan a editar; si es null, se asume modo agregar.
 * @param onSave  Callback para guardar el plan (nuevo o editado).
 * @param onFork  Callback para bifurcar el plan.
 * @param onClose Callback para acciones al cerrar el diálogo.
 */
class DialogNuevoPlan(
    context: Context,
    private val plan: Plan? = null,
    private val onSave: (Plan) -> Unit,
    private val onFork: (Plan) -> Unit,
    private val onClose: () -> Unit
) {
    private val binding: DialogNuevoPlanBinding =
        DialogNuevoPlanBinding.inflate(LayoutInflater.from(context))
    private val dialog: Dialog = MaterialAlertDialogBuilder(context)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    // Variable para alternar entre campos de acción y reacción.
    private var ultimoCampoAccion = true

    init {
        // Si se pasa un plan, precargar los datos para editar.
        // Si se pasa un plan, precargar los datos para editar.
        plan?.let {
            binding.etAccionInicial.setText(it.primeraPosicion)
            binding.tilAccionInicial.error = null
            binding.llCamposDinamicos.removeAllViews()
            val numActions = it.acciones.size
            val numReactions = it.reacciones.size
            val numDynamicFields = maxOf(numActions - 1, numReactions)
            for (i in 0 until numDynamicFields) {
                // Precargar campo de acción (a partir del segundo elemento)
                if (i + 1 < numActions) {
                    val actionView = LayoutInflater.from(context)
                        .inflate(com.sukata.bjj.R.layout.item_accion, binding.llCamposDinamicos, false)
                    val etAccion = actionView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.sukata.bjj.R.id.etAccionDinamico)
                    etAccion.setText(it.acciones[i + 1])
                    binding.llCamposDinamicos.addView(actionView)
                }
                // Precargar campo de reacción
                if (i < numReactions) {
                    val reactionView = LayoutInflater.from(context)
                        .inflate(com.sukata.bjj.R.layout.item_reaccion, binding.llCamposDinamicos, false)
                    val etReaccion = reactionView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.sukata.bjj.R.id.etReaccionDinamico)
                    etReaccion.setText(it.reacciones[i])
                    binding.llCamposDinamicos.addView(reactionView)
                }
            }

            // Actualizar la variable de control para que el siguiente campo agregado
            // tenga el tipo correcto.
            val countAcciones = numActions - 1 // se resta 1 porque la primera acción ya se usó en etAccionInicial
            val countReacciones = numReactions
            ultimoCampoAccion = if (countAcciones > countReacciones) {
                // Último campo pre-cargado fue una acción → próximo será una reacción.
                true
            } else {
                // Último campo pre-cargado fue una reacción (o son iguales) → próximo será acción.
                false
            }
        }


        // Configurar botón para agregar campos dinámicos.
        binding.fabNuevoPaso.setOnClickListener {
            agregarCampo(context)
        }

        // Botón Guardar: valida y envía el plan a guardar.
        binding.btnGuardar.setOnClickListener {
            val nuevoPlan = recogerDatosDelDialogo()
            if (nuevoPlan != null) {
                onSave(nuevoPlan)
                dialog.dismiss()
            }
        }

        // Botón Bifurcar: guarda el plan actual y crea uno nuevo a partir del último campo.
        binding.btnBifurcar.setOnClickListener {
            val nuevoPlan = recogerDatosDelDialogo()
            if (nuevoPlan != null) {
                onSave(nuevoPlan)
            }
            val planActualizado = recogerDatosDelDialogo()
            if (planActualizado != null) {
                // Se obtiene la nueva posición a partir del último campo dinámico.
                val nuevaPosicion = obtenerNuevaPosicion(context)
                // Se puede actualizar el plan original antes de bifurcar.
                onFork(planActualizado.copy(primeraPosicion = nuevaPosicion))
                // Reiniciar el diálogo para editar el nuevo plan:
                binding.etAccionInicial.setText(nuevaPosicion)
                binding.llCamposDinamicos.removeAllViews()
                ultimoCampoAccion = true
            }
        }

        // Botón Cerrar: cierra el diálogo y ejecuta el callback.
        binding.btnCerrar.setOnClickListener {
            dialog.dismiss()
            onClose()
        }
    }

    fun show() {
        dialog.show()
    }

    // Función para agregar un campo dinámico alternando entre acción y reacción.
    private fun agregarCampo(context: Context) {
        val inflater = LayoutInflater.from(context)
        if (ultimoCampoAccion) {
            // Agregar campo de reacción.
            val nuevaReaccion = inflater.inflate(com.sukata.bjj.R.layout.item_reaccion, binding.llCamposDinamicos, false)
            binding.llCamposDinamicos.addView(nuevaReaccion)
            ultimoCampoAccion = false
        } else {
            // Agregar campo de acción.
            val nuevaAccion = inflater.inflate(com.sukata.bjj.R.layout.item_accion, binding.llCamposDinamicos, false)
            binding.llCamposDinamicos.addView(nuevaAccion)
            ultimoCampoAccion = true
        }
    }

    // Recoge los datos ingresados en el diálogo y retorna un objeto Plan; si hay error, retorna null.
    private fun recogerDatosDelDialogo(): Plan? {
        val accionInicial = binding.etAccionInicial.text.toString().trim()
        if (accionInicial.isEmpty()) {
            binding.tilAccionInicial.error = "Este campo es requerido"
            return null
        } else {
            binding.tilAccionInicial.error = null
        }
        val acciones = mutableListOf<String>()
        val reacciones = mutableListOf<String>()
        acciones.add(accionInicial)
        for (i in 0 until binding.llCamposDinamicos.childCount) {
            val campo = binding.llCamposDinamicos.getChildAt(i)
            val tilAccion = campo.findViewById<com.google.android.material.textfield.TextInputLayout>(com.sukata.bjj.R.id.tilAccionDinamico)
            val tilReaccion = campo.findViewById<com.google.android.material.textfield.TextInputLayout>(com.sukata.bjj.R.id.tilReaccionDinamico)
            tilAccion?.let {
                val text = it.editText?.text.toString().trim()
                if (text.isEmpty()) {
                    it.error = "Este campo es requerido"
                    return null
                } else {
                    it.error = null
                }
                acciones.add(text)
            }
            tilReaccion?.let {
                val text = it.editText?.text.toString().trim()
                if (text.isEmpty()) {
                    it.error = "Este campo es requerido"
                    return null
                } else {
                    it.error = null
                }
                reacciones.add(text)
            }
        }
        return Plan(
            primeraPosicion = accionInicial,
            acciones = acciones,
            reacciones = reacciones
        )
    }

    // Obtiene la nueva posición a partir del último campo dinámico; si no hay, usa el campo inicial.
    private fun obtenerNuevaPosicion(context: Context): String {
        val childCount = binding.llCamposDinamicos.childCount
        if (childCount > 0) {
            val lastChild = binding.llCamposDinamicos.getChildAt(childCount - 1)
            val tilAccion = lastChild.findViewById<com.google.android.material.textfield.TextInputLayout>(com.sukata.bjj.R.id.tilAccionDinamico)
            val tilReaccion = lastChild.findViewById<com.google.android.material.textfield.TextInputLayout>(com.sukata.bjj.R.id.tilReaccionDinamico)
            if (tilAccion != null) {
                return tilAccion.editText?.text.toString().trim()
            } else if (tilReaccion != null) {
                return tilReaccion.editText?.text.toString().trim()
            }
        }
        return binding.etAccionInicial.text.toString().trim()
    }
}
