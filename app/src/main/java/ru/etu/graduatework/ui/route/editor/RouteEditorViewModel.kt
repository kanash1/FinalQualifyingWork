package ru.etu.graduatework.ui.route.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.etu.graduatework.core.ui.BaseViewModel

class RouteEditorViewModel :
    BaseViewModel() {

    fun goToSignIn() {
        navigate(RouteEditorFragmentDirections.actionRouteEditorFragment2ToSignInFragment2())
    }

    fun goToRouteNavigation() {
        navigate(RouteEditorFragmentDirections.actionRouteEditorFragment2ToRouteNavigationFragment())
    }

    fun goToOptimizationDialog() {
        navigate(RouteEditorFragmentDirections.actionRouteEditorFragment2ToOptimizationDialogFragment())
    }

    fun goToSaveDialog(name: String?) {
        navigate(RouteEditorFragmentDirections.actionRouteEditorFragment2ToSaveRouteDialogFragment(name))
    }

    fun goToLoadSavedRoutesFragment() {
        navigate(RouteEditorFragmentDirections.actionRouteEditorFragment2ToLoadSavedRoutesFragment())
    }

    fun goToAccountFragment() {
        navigate(RouteEditorFragmentDirections.actionRouteEditorFragment2ToAccountFragment())
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RouteEditorViewModel::class.java)) {
                return RouteEditorViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}