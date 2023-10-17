package ru.etu.graduatework.ui.location

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.openAppSettings
import ru.etu.graduatework.core.ui.BaseDialogFragment
import ru.etu.graduatework.ui.MainActivity
import ru.etu.graduatework.ui.map.MapFragment

class PermissionDialogFragment : BaseDialogFragment<PermissionViewModel>() {
    override val viewModel: PermissionViewModel by viewModels()

    companion object {
        @JvmStatic
        val ARG_IS_PERMANENTLY_DECLINED = "ARG_IS_PERMANENTLY_DECLINED"

        @JvmStatic
        fun newInstance(isPermanentlyDeclined: Boolean): PermissionDialogFragment {
            val args =
                Bundle().apply { putBoolean(ARG_IS_PERMANENTLY_DECLINED, isPermanentlyDeclined) }
            val fragment = PermissionDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun isPermanentlyDeclined() =
        requireArguments().getBoolean(PermissionDialogFragment.ARG_IS_PERMANENTLY_DECLINED)

    private fun getDescriptionId(): Int {
        return if (isPermanentlyDeclined())
            R.string.location_permission_permanently_declined
        else
            R.string.location_permission_declined
    }

    private fun getPositiveButtonTextId(): Int {
        return if (isPermanentlyDeclined())
            R.string.go_to_settings
        else
            R.string.OK
    }

    private fun getAction(): () -> Unit {
        return if (isPermanentlyDeclined())
            requireActivity()::openAppSettings
        else
            (requireParentFragment() as MapFragment)::launchLocationPermission
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it, R.style.AlertDialog)
            builder
                .setTitle(R.string.permission)
                .setMessage(getDescriptionId())
                .setPositiveButton(getPositiveButtonTextId()) { _, _ ->
                    viewModel.navigateBack()
                    getAction().invoke()
                }
                .setNegativeButton(R.string.dismiss) { _, _ ->
                    viewModel.navigateBack()
                }
            return@let builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}