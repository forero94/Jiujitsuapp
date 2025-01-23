package com.sukata.bjj.ui.tecnicas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.sukata.bjj.data.entities.TecnicaEntity
import com.sukata.bjj.databinding.DialogDetalleTecnicaBinding

class DetalleTecnicaDialogFragment : DialogFragment() {

    private var _binding: DialogDetalleTecnicaBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_TECNICA = "arg_tecnica"

        // Método para crear una instancia del diálogo con los argumentos
        fun newInstance(tecnica: TecnicaEntity): DetalleTecnicaDialogFragment {
            val fragment = DetalleTecnicaDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_TECNICA, tecnica) // Usar putParcelable para un objeto Parcelable
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDetalleTecnicaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar la técnica desde los argumentos
        val tecnica = arguments?.getParcelable<TecnicaEntity>(ARG_TECNICA)
        tecnica?.let {
            // Configurar los datos en la vista
            binding.tvNombreTecnica.text = it.nombre
            binding.tvPosicionInicial.text = "Posición Inicial: ${it.posicionInicial}"
            binding.tvTipo.text = "Tipo: ${it.tipoTecnica}"
            binding.tvObjetivoPrincipal.text = "Objetivo: ${it.objetivoPrincipal}"
            binding.tvDificultad.text = "Dificultad: ${it.dificultad?.name}"
            binding.tvContexto.text = "Contexto: ${it.contexto}"
            binding.tvDescripcion.text = it.descripcion ?: "Sin descripción"

            // Configurar recursos adicionales
            if (!it.recursosAdicionales.isNullOrEmpty()) {
                binding.tvRecursosTitulo.visibility = View.VISIBLE
                binding.tvRecursos.visibility = View.VISIBLE
                binding.tvRecursos.text = it.recursosAdicionales.joinToString(separator = "\n") { recurso ->
                    "- $recurso"
                }
            } else {
                binding.tvRecursosTitulo.visibility = View.GONE
                binding.tvRecursos.visibility = View.GONE
            }
        }

        // Configurar el botón de cerrar
        binding.btnCerrar.setOnClickListener {
            dismiss()
        }
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(), // 90% del ancho de la pantalla
            ViewGroup.LayoutParams.WRAP_CONTENT // Ajusta la altura automáticamente
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
