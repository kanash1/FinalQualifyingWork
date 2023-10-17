package ru.etu.graduatework.ui.interaction

import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.etu.graduatework.core.extension.share
import ru.etu.graduatework.core.ui.BaseViewModel

class InteractionWithMapViewModel : BaseViewModel() {

    private val _bottomSheetState = MutableLiveData(BottomSheetBehavior.STATE_HIDDEN)
    val bottomSheetState = _bottomSheetState.share()

    fun setBottomSheetState(state: Int) {
        if (_bottomSheetState.value != state)
            _bottomSheetState.value = state
    }

    fun goToRouteEditor() {
        navigate(InteractionWithMapFragmentDirections.actionInteractionWithMapFragmentToRouteEditorFragment2())
    }

    fun goToSearch() {
        navigate(InteractionWithMapFragmentDirections.actionInteractionWithMapFragmentToSearchFragment())
    }
}