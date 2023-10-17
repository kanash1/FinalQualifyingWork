package ru.etu.graduatework.ui.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.addTextChangedListener
import ru.etu.graduatework.core.extension.gone
import ru.etu.graduatework.core.extension.makeLinks
import ru.etu.graduatework.core.extension.visible
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.core.utils.observeEvent
import ru.etu.graduatework.databinding.FragmentSignInBinding
import ru.etu.graduatework.ui.MainViewModel

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding, SignInViewModel>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSignInBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentSignInBinding.inflate(
                layoutInflater,
                viewGroup,
                attachToParent
            )
        }

    override val viewModel: SignInViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSignIn()
        observeFailure()

        with(binding) {
            btnSignIn.setOnClickListener { signIn() }
            toolbar.setNavigationOnClickListener { viewModel.navigateBack() }

            tvSignUp.makeLinks(
                Pair(resources.getString(R.string.action_sign_up), View.OnClickListener {
                    goToSignUp()
                })
            )

            etUsername.addTextChangedListener(onTextChanged = { s, _, _, _ ->
                tvUsernameError.gone()
            })

            etPassword.addTextChangedListener(onTextChanged = { s, _, _, _ ->
                tvPasswordError.gone()
            })
        }
    }

    private fun observeSignIn() = viewModel.signInEvent.observeEvent(viewLifecycleOwner) {
        activityViewModel.isAuth()
        viewModel.navigateBack()
    }

    private fun observeFailure() = viewModel.failureEvent.observeEvent(viewLifecycleOwner) {
        viewModel.goToErrorDialog(R.string.wrong_sign_in_data)
    }

    private fun signIn() =
        viewModel.signIn(binding.etUsername.text.toString(), binding.etPassword.text.toString())

    private fun goToSignUp() = viewModel.goToSignUp(binding.etUsername.text.toString())
}