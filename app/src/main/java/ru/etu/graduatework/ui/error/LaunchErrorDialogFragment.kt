package ru.etu.graduatework.ui.error

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.etu.graduatework.R
import ru.etu.graduatework.core.ui.BaseDialogFragment
import ru.etu.graduatework.ui.MainViewModel
import kotlin.system.exitProcess

class LaunchErrorDialogFragment : BaseDialogFragment<LaunchErrorViewModel>() {
    override val viewModel: LaunchErrorViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it, R.style.AlertDialog)
            builder
                .setTitle(R.string.network_error_message)
                .setMessage(R.string.network_error_description)
                .setPositiveButton(R.string.retry) { _, _ ->
                    activityViewModel.loadAttractions()
                    viewModel.navigateBack()
                }
                .setNegativeButton(R.string.dismiss) { _, _ ->
                    it.finishAffinity()
                    exitProcess(0)
                }
            return@let builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}