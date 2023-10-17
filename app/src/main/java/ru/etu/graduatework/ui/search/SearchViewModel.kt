package ru.etu.graduatework.ui.search

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.etu.graduatework.core.extension.share
import ru.etu.graduatework.core.ui.BaseViewModel
import ru.etu.graduatework.core.utils.MutableLiveEvent
import ru.etu.graduatework.core.utils.publishEvent
import ru.etu.graduatework.domain.interactor.SearchUseCase
import ru.etu.graduatework.domain.model.LiteAttraction
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(val search: SearchUseCase) : BaseViewModel() {
    private val _searchEvent = MutableLiveEvent<Collection<LiteAttraction>>()
    val searchEvent = _searchEvent.share()

    fun goToMap(attractionId: Int) {
        navigate(
            SearchFragmentDirections.actionSearchFragmentToInteractionWithMapFragment(
                attractionId
            )
        )
    }

    fun submitSearchQuery(query: String, collection: Collection<LiteAttraction>) =
        search((query to collection), viewModelScope) {
            it.fold(::handleSearch, ::handleFailure)
        }

    private fun handleSearch(results: Collection<LiteAttraction>) {
        _searchEvent.publishEvent(results)
    }
}