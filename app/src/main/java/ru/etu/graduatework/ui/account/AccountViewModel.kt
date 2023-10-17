package ru.etu.graduatework.ui.account

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.etu.graduatework.core.ui.BaseViewModel
import ru.etu.graduatework.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {
    fun logout() = viewModelScope.launch {
        authRepository.logout()
            .onSuccess { navigateBack() }
            .onFailure { handleFailure(it) }
    }
}