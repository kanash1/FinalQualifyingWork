package ru.etu.graduatework.ui.entry

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
class SignInViewModel @Inject constructor(private val authRepository: AuthRepository) :
    BaseViewModel() {
    private val _signInEvent = MutableUnitLiveEvent()
    val signInEvent = _signInEvent.share()

    fun signIn(username: CharSequence?, password: CharSequence?) = viewModelScope.launch {
        if (!username.isNullOrEmpty() && !password.isNullOrEmpty())
            authRepository.signIn(username.toString(), password.toString()).fold(
                { _signInEvent.publishEvent() },
                ::handleFailure
            )
    }

    fun goToSignUp(username: String) {
        val usernameArg = username.ifBlank { null }
        navigate(SignInFragmentDirections.actionSignInFragment2ToSignUpFragment2(usernameArg))
    }

    fun goToErrorDialog(messageId: Int) {
        navigate(SignInFragmentDirections.actionSignInFragment2ToErrorDialogFragment(messageId))
    }
}