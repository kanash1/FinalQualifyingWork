package ru.etu.graduatework.ui.route.save

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.dpToPx
import ru.etu.graduatework.core.ui.BaseDialogFragment
import ru.etu.graduatework.ui.MainViewModel

class SaveRouteDialogFragment : BaseDialogFragment<SaveRouteViewModel>() {
    override val viewModel: SaveRouteViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val args: SaveRouteDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it, R.style.AlertDialog)
            val etRouteName = EditText(requireContext())
            etRouteName.setPadding(16f.dpToPx())
            etRouteName.hint = resources.getText(R.string.route_name)
            etRouteName.setText(args.routeName)

            builder
                .setTitle(R.string.save_route)
//                .setView(inflater.inflate(R.layout.fragment_save_route, null))
                .setView(etRouteName)
                .setPositiveButton(R.string.OK) { _, _ ->
//                    val etRouteName = requireView().findViewById<EditText>(R.id.ed_route_name)
                    if (!etRouteName.text.isNullOrEmpty()) {
                        activityViewModel.saveRouteWithName(etRouteName.text.toString())
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