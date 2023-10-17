package ru.etu.graduatework.ui.route.load

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.gone
import ru.etu.graduatework.core.extension.visible
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.core.utils.observeEvent
import ru.etu.graduatework.databinding.FragmentSavedRoutesBinding
import ru.etu.graduatework.ui.MainViewModel
import ru.etu.graduatework.ui.map.MapFragment

@AndroidEntryPoint
class LoadSavedRoutesFragment :
    BaseFragment<FragmentSavedRoutesBinding, LoadSavedRoutesViewModel>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSavedRoutesBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentSavedRoutesBinding.inflate(layoutInflater, viewGroup, attachToParent)
        }

    override val viewModel: LoadSavedRoutesViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val mapFragment: MapFragment by lazy {
        val fragmentManager = requireActivity().supportFragmentManager
        val navHostFragment = fragmentManager.findFragmentById(R.id.fragment_container)
        val fragments = navHostFragment!!.childFragmentManager.fragments
        fragments[fragments.lastIndex] as MapFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSavedRoutes.gone()
        binding.progressbar.visible()

        observeFailure()
        observeSavedRoutes()
        observeLoadRouteEvent()

        val adapter = LoadSavedRoutesAdapter()

        adapter.setOnDeleteClickListener(object : LoadSavedRoutesAdapter.OnClickListener {
            override fun onClick(position: Int) {
                viewModel.deleteRouteAt(position)
                adapter.removePointAt(position)
            }
        })

        adapter.setOnItemClickListener(object : LoadSavedRoutesAdapter.OnClickListener {
            override fun onClick(position: Int) {
                activityViewModel.getRouteByName(viewModel.savedRoutes.value!![position])
                adapter.isEnabled = false
            }
        })

        binding.rvSavedRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSavedRoutes.adapter = adapter

        binding.toolbar.setNavigationOnClickListener { viewModel.navigateBack() }
    }

    private fun observeSavedRoutes() = viewModel.savedRoutes.observe(viewLifecycleOwner) {
        val adapter = binding.rvSavedRoutes.adapter as LoadSavedRoutesAdapter
        adapter.updateAll(it.toMutableList())
        binding.progressbar.gone()
        binding.rvSavedRoutes.visible()
        adapter.isEnabled = true
    }

    private fun observeLoadRouteEvent() =
        activityViewModel.loadRouteEvent.observeEvent(viewLifecycleOwner) {
            mapFragment.updatePlacemarks()
            viewModel.navigateBack()
        }

    private fun observeFailure() = activityViewModel.failureEvent.observeEvent(viewLifecycleOwner) {
        val adapter = binding.rvSavedRoutes.adapter as LoadSavedRoutesAdapter
        if (!viewModel.savedRoutes.value.isNullOrEmpty())
            adapter.updateAll(viewModel.savedRoutes.value!!.toMutableList())
        Toast.makeText(context, R.string.network_error_message, Toast.LENGTH_SHORT).show()
        adapter.isEnabled = true
    }
}