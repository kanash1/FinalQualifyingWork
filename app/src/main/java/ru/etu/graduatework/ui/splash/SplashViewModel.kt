package ru.etu.graduatework.ui.splash

import ru.etu.graduatework.core.ui.BaseViewModel

class SplashViewModel : BaseViewModel() {
    fun goToMap() {
        navigate(SplashFragmentDirections.actionSplashFragment2ToMapFragment2())
    }

    fun goToError() {
        navigate(SplashFragmentDirections.actionSplashFragment2ToLaunchErrorDialogFragment())
    }
}