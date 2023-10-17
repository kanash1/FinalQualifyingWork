package ru.etu.graduatework.core.ui

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import ru.etu.graduatework.core.exception.Failure
import ru.etu.graduatework.core.extension.share
import ru.etu.graduatework.core.navigation.NavigationCommand
import ru.etu.graduatework.core.utils.MutableLiveEvent
import ru.etu.graduatework.core.utils.publishEvent

abstract class BaseViewModel : ViewModel() {
    // событие навигации
    private val _navigationEvent = MutableLiveEvent<NavigationCommand>()
    val navigationEvent = _navigationEvent.share()
    // событие ошибки
    private val _failureEvent = MutableLiveEvent<Failure>()
    val failureEvent = _failureEvent.share()
    // создание события для навигации к указаному фрагменту
    fun navigate(navDirections: NavDirections) =
        _navigationEvent.publishEvent(
            NavigationCommand.ToDirection(navDirections)
        )
    // создание события для навигации к предыдущему фрагменту
    fun navigateBack() {
        _navigationEvent.publishEvent(NavigationCommand.Back)
    }
    // создание события ошибки
    protected fun handleFailure(failure: Throwable) {
        _failureEvent.publishEvent(failure as Failure)
    }
}