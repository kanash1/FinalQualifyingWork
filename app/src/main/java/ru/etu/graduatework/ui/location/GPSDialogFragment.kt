package ru.etu.graduatework.ui.location

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.openLocationSettings
import ru.etu.graduatework.core.ui.BaseDialogFragment

class GPSDialogFragment : BaseDialogFragment<GPSViewModel>() {
    override val viewModel: GPSViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it, R.style.AlertDialog)
            builder
                .setMessage(R.string.gps_turn_on)
                .setPositiveButton(R.string.OK) { _, _ ->
                    requireActivity().openLocationSettings()
                }
                .setNegativeButton(R.string.dismiss) { _, _ ->
                    viewModel.navigateBack()
                }
            return@let builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}