package ru.etu.graduatework.ui.route.editor

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.etu.graduatework.R
import ru.etu.graduatework.core.ui.BaseDialogFragment
import ru.etu.graduatework.ui.MainViewModel
import ru.etu.graduatework.ui.map.MapFragment

class OptimizationDialogFragment : BaseDialogFragment<OptimizationViewModel>() {
    override val viewModel: OptimizationViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val mapFragment: MapFragment by lazy {
        val fragmentManager = requireActivity().supportFragmentManager
        val navHostFragment = fragmentManager.findFragmentById(R.id.fragment_container)
        val fragments = navHostFragment!!.childFragmentManager.fragments
        fragments[fragments.lastIndex] as MapFragment
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val selectedItems = ArrayList<Int>()
            val builder = MaterialAlertDialogBuilder(it, R.style.AlertDialog)
            builder
                .setMultiChoiceItems(R.array.optimize_options, null) { _, which, isChecked ->
                    if (isChecked) {
                        selectedItems.add(which)
                    } else if (selectedItems.contains(which)) {
                        selectedItems.remove(which)
                    }
                }
                .setPositiveButton(R.string.OK) { _, _ ->
                    if (activityViewModel.currentRoute.value!!.useLocation) {
                        val point = mapFragment.getUserLocation()
                        if (point == null) {
                            Toast.makeText(
                                context,
                                R.string.cannot_get_location,
                                Toast.LENGTH_LONG).show()
                        } else {
                            activityViewModel.optimizeRoute(
                                selectedItems.contains(0),
                                selectedItems.contains(1),
                                point
                            )
                            viewModel.navigateBack()
                        }
                    } else {
                        activityViewModel.optimizeRoute(
                            selectedItems.contains(0),
                            selectedItems.contains(1),
                            null
                        )
                        viewModel.navigateBack()
                    }
                }
                .setNegativeButton(R.string.dismiss) { _, _ ->
                    viewModel.navigateBack()
                }
            return@let builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}