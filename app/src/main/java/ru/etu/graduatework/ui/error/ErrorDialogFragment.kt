package ru.etu.graduatework.ui.error

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.etu.graduatework.R
import ru.etu.graduatework.core.ui.BaseDialogFragment

class ErrorDialogFragment : BaseDialogFragment<ErrorViewModel>() {
    override val viewModel: ErrorViewModel by viewModels()
    private val args: ErrorDialogFragmentArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it, R.style.AlertDialog)
            builder
                .setMessage(args.messageId)
                .setPositiveButton(R.string.OK) { _, _ ->
                    viewModel.navigateBack()
                }
            return@let builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}