package ru.etu.graduatework.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.etu.graduatework.R
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.databinding.FragmentAccountBinding

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding, AccountViewModel>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAccountBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentAccountBinding.inflate(layoutInflater, viewGroup, attachToParent)
        }
    override val viewModel: AccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.btnDeleteAccount.setOnClickListener {}

        binding.toolbar.setNavigationOnClickListener {
            viewModel.navigateBack()
        }

        viewModel.failureEvent.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), R.string.network_error_message, Toast.LENGTH_SHORT)
                .show()
        }
    }
}