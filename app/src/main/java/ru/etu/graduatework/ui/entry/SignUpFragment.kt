package ru.etu.graduatework.ui.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.addTextChangedListener
import ru.etu.graduatework.core.extension.gone
import ru.etu.graduatework.core.extension.makeLinks
import ru.etu.graduatework.core.extension.visible
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.core.utils.observeEvent
import ru.etu.graduatework.databinding.FragmentSignUpBinding

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSignUpBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentSignUpBinding.inflate(
                layoutInflater,
                viewGroup,
                attachToParent
            )
        }

    private val args by navArgs<SignUpFragmentArgs>()
    override val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null && args.username != null) {
            binding.etUsername.setText(args.username)
        }

        observeInputError()
        observeSignUpEvent()
        observeFailureEvent()

        with(binding) {
            btnSignUp.setOnClickListener { signUp() }
            toolbar.setNavigationOnClickListener { viewModel.navigateBack() }

            tvSignIn.makeLinks(
                Pair(resources.getString(R.string.action_sign_in), View.OnClickListener {
                    viewModel.navigateBack()
                })
            )

            etEmail.addTextChangedListener(onTextChanged = { s, _, _, _ ->
                tvEmailError.gone()
                viewModel.signUpDataChanged(s, etUsername.editableText, etPassword.editableText)
            })

            etUsername.addTextChangedListener(onTextChanged = { s, _, _, _ ->
                tvUsernameError.gone()
                viewModel.signUpDataChanged(etEmail.editableText, s, etPassword.editableText)
            })

            etPassword.addTextChangedListener(onTextChanged = { s, _, _, _ ->
                tvPasswordError.gone()
                viewModel.signUpDataChanged(etEmail.editableText, etUsername.editableText, s)
            })

            etConfirmPassword.addTextChangedListener(onTextChanged = { s, _, _, _ ->
                tvConfirmPasswordError.gone()
            })
        }
    }

    private fun observeInputError() = viewModel.inputErrorEvent.observeEvent(viewLifecycleOwner) {
        with(binding) {
            when(it) {
                SignUpError.INVALID_EMAIL -> {
                    tvEmailError.text = resources.getText(R.string.email_is_invalid)
                    tvEmailError.visible()
                }
                SignUpError.INVALID_USERNAME -> {
                    tvUsernameError.text = resources.getText(R.string.username_is_invalid)
                    tvUsernameError.visible()
                }
                SignUpError.INVALID_PASSWORD -> {
                    tvPasswordError.text = resources.getText(R.string.password_is_invalid)
                    tvPasswordError.visible()
                }
                SignUpError.USERNAME_IS_TAKEN -> {
                    tvUsernameError.text = resources.getText(R.string.username_is_taken)
                    tvUsernameError.visible()
                }
                SignUpError.ACCOUNT_ALREADY_EXISTS -> {
                    tvEmailError.text = resources.getText(R.string.account_already_exists)
                    tvEmailError.visible()
                }
                SignUpError.PASSWORD_MISMATCH -> {
                    tvConfirmPasswordError.text = resources.getText(R.string.password_mismatch)
                    tvConfirmPasswordError.visible()
                }
            }
        }
    }

    private fun observeSignUpEvent() = viewModel.signUpEvent.observeEvent(viewLifecycleOwner) {
        viewModel.goToActivateAccountDialog()
    }

    private fun observeFailureEvent() = viewModel.failureEvent.observeEvent(viewLifecycleOwner) {
        viewModel.goToErrorDialog(R.string.wrong_sign_up_data)
    }

    private fun signUp() = with(binding) {
        viewModel.signUp(etEmail.text, etUsername.text, etPassword.text, etConfirmPassword.text)
    }
}