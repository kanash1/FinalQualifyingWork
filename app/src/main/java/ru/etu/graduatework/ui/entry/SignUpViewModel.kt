package ru.etu.graduatework.ui.entry

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.etu.graduatework.core.extension.isValidEmail
import ru.etu.graduatework.core.extension.isValidPassword
import ru.etu.graduatework.core.extension.isValidUsername
import ru.etu.graduatework.core.extension.share
import ru.etu.graduatework.core.ui.BaseViewModel
import ru.etu.graduatework.core.utils.MutableLiveEvent
import ru.etu.graduatework.core.utils.MutableUnitLiveEvent
import ru.etu.graduatework.core.utils.publishEvent
import ru.etu.graduatework.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val authRepository: AuthRepository) :
    BaseViewModel() {
    private val _signUpEvent = MutableUnitLiveEvent()
    val signUpEvent = _signUpEvent.share()

    private val _inputErrorEvent = MutableLiveEvent<SignUpError>()
    val inputErrorEvent = _inputErrorEvent.share()

    fun signUp(
        email: CharSequence?,
        username: CharSequence?,
        password: CharSequence?,
        confirmPassword: CharSequence?
    ) {
        val isValidEmail = email.isValidEmail()
        val isValidUsername = username.isValidUsername()
        val isValidPassword = password.isValidPassword()
        val confirmed = password.contentEquals(confirmPassword)

        if (isValidEmail && isValidUsername && isValidPassword && confirmed) {
            viewModelScope.launch {
                authRepository.signUp(
                    email!!.toString(),
                    username!!.toString(),
                    password!!.toString()
                ).fold(
                    { _signUpEvent.publishEvent() },
                    ::handleFailure
                )
            }
        } else {
            if (!isValidUsername)
                _inputErrorEvent.publishEvent(SignUpError.INVALID_USERNAME)
            if (!isValidPassword)
                _inputErrorEvent.publishEvent(SignUpError.INVALID_PASSWORD)
            if (!isValidEmail)
                _inputErrorEvent.publishEvent(SignUpError.INVALID_EMAIL)
            if (!confirmed)
                _inputErrorEvent.publishEvent(SignUpError.PASSWORD_MISMATCH)
        }
    }

    fun signUpDataChanged(email: CharSequence?, username: CharSequence?, password: CharSequence?) {
        if (!username.isNullOrEmpty() && !username.isValidUsername())
            _inputErrorEvent.publishEvent(SignUpError.INVALID_USERNAME)
        if (!password.isNullOrEmpty() && !password.isValidPassword())
            _inputErrorEvent.publishEvent(SignUpError.INVALID_PASSWORD)
        if (!email.isNullOrEmpty() && !email.isValidEmail())
            _inputErrorEvent.publishEvent(SignUpError.INVALID_EMAIL)
    }

    fun goToErrorDialog(messageId: Int) {
        navigate(SignUpFragmentDirections.actionSignUpFragment2ToErrorDialogFragment(messageId))
    }

    fun goToActivateAccountDialog() {
        navigate(SignUpFragmentDirections.actionSignUpFragment2ToActivateAccountDialogFragment())
    }
}

enum class SignUpError {
    INVALID_EMAIL,
    INVALID_USERNAME,
    INVALID_PASSWORD,
    USERNAME_IS_TAKEN,
    ACCOUNT_ALREADY_EXISTS,
    PASSWORD_MISMATCH
}