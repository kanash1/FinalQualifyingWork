package ru.etu.graduatework.ui.attraction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.etu.graduatework.core.extension.share
import ru.etu.graduatework.core.ui.BaseViewModel
import ru.etu.graduatework.domain.model.Attraction
import ru.etu.graduatework.domain.interactor.GetAttractionUseCase

class AttractionViewModel @AssistedInject constructor(
    @Assisted private val id: Int,
    private val getAttractionUseCase: GetAttractionUseCase
) : BaseViewModel() {

    private var _attraction = MutableLiveData<Attraction>()
    val attraction = _attraction.share()

    init {
        load()
    }

    fun load() = getAttractionUseCase(id, viewModelScope) { result ->
        result.fold(::handleAttraction, ::handleFailure)
    }

    private fun handleAttraction(attraction: Attraction) {
        _attraction.value = attraction
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): AttractionViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(assistedFactory: Factory, id: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return assistedFactory.create(id) as T
                }
            }
    }
}