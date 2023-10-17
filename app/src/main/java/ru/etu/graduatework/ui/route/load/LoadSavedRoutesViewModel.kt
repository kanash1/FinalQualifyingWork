package ru.etu.graduatework.ui.route.load

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.etu.graduatework.core.extension.share
import ru.etu.graduatework.core.ui.BaseViewModel
import ru.etu.graduatework.domain.repository.RoutesRepository
import javax.inject.Inject

@HiltViewModel
class LoadSavedRoutesViewModel @Inject constructor(
    private val routesRepository: RoutesRepository
) : BaseViewModel() {
    private val _savedRoutes = MutableLiveData<MutableList<String>>()
    val savedRoutes = _savedRoutes.share()

    init {
        viewModelScope.launch {
            routesRepository.getRoutes().fold(
                { _savedRoutes.value = it.toMutableList() },
                ::handleFailure
            )
        }
    }

    fun deleteRouteAt(index: Int) {
        viewModelScope.launch {
            routesRepository.removeRoute(_savedRoutes.value!![index]).fold(
                { _savedRoutes.value?.removeAt(index) },
                ::handleFailure
            )
        }
    }
}