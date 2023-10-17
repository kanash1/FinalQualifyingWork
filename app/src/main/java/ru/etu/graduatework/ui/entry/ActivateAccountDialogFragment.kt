package ru.etu.graduatework.ui.entry

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.etu.graduatework.R
import ru.etu.graduatework.core.ui.BaseDialogFragment

class ActivateAccountDialogFragment : BaseDialogFragment<ActivateAccountViewModel>() {
    override val viewModel: ActivateAccountViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it, R.style.AlertDialog)
            builder
                .setTitle(R.string.success)
                .setMessage(R.string.go_through_account_activation)
                .setPositiveButton(R.string.OK) { _, _ ->
                    viewModel.navigateBack()
                }
            return@let builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}