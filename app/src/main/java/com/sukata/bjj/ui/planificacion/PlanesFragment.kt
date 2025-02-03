package com.sukata.bjj.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sukata.bjj.data.entities.Plan
import com.sukata.bjj.databinding.FragmentPlanesBinding
import com.sukata.bjj.ui.planificacion.DialogNuevoPlan
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
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PlanesAdapter(
            onItemClick = { plan ->
                // Para editar o bifurcar, invoca el diálogo pasando el plan actual.
                DialogNuevoPlan(
                    context = requireContext(),
                    plan = plan,
                    onSave = { planActualizado ->
                        planViewModel.update(planActualizado)
                        Toast.makeText(requireContext(), "Plan actualizado", Toast.LENGTH_SHORT).show()
                    },
                    onFork = { planFork ->
                        // Aquí se maneja la bifurcación.
                        planViewModel.insert(planFork)
                        Toast.makeText(requireContext(), "Plan bifurcado", Toast.LENGTH_SHORT).show()
                    },
                    onClose = {
                        // Acción al cerrar el diálogo, si es necesario.
                    }
                ).show()
            },
            onDeleteClick = { plan ->
                planViewModel.delete(plan)
                Toast.makeText(requireContext(), "Plan eliminado", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewPlanes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPlanes.adapter = adapter

        binding.fabNuevoPlan.setOnClickListener {
            // Para agregar, se invoca el diálogo sin pasar un plan (modo agregar).
            DialogNuevoPlan(
                context = requireContext(),
                plan = null,
                onSave = { nuevoPlan ->
                    planViewModel.insert(nuevoPlan)
                    Toast.makeText(requireContext(), "Nuevo plan agregado", Toast.LENGTH_SHORT).show()
                },
                onFork = { planFork ->
                    planViewModel.insert(planFork)
                    Toast.makeText(requireContext(), "Plan bifurcado", Toast.LENGTH_SHORT).show()
                },
                onClose = { }
            ).show()
        }

        planViewModel.allPlanes.observe(viewLifecycleOwner) { planes ->
            planes?.let {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
