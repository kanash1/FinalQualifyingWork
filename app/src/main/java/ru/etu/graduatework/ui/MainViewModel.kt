// автор: Медведев О. В.

package ru.etu.graduatework.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.etu.graduatework.core.exception.Failure
import ru.etu.graduatework.core.extension.notifyObserver
import ru.etu.graduatework.core.extension.share
import ru.etu.graduatework.core.extension.toInt
import ru.etu.graduatework.core.ui.BaseViewModel
import ru.etu.graduatework.core.utils.MutableLiveEvent
import ru.etu.graduatework.core.utils.MutableUnitLiveEvent
import ru.etu.graduatework.core.utils.publishEvent
import ru.etu.graduatework.domain.interactor.GetAttractionsUseCase
import ru.etu.graduatework.domain.model.LiteAttraction
import ru.etu.graduatework.domain.model.OptimizationData
import ru.etu.graduatework.domain.model.Route
import ru.etu.graduatework.domain.distanceMatrix
import ru.etu.graduatework.domain.repository.AttractionsRepository
import ru.etu.graduatework.domain.repository.AuthRepository
import ru.etu.graduatework.domain.repository.RoutesRepository
import java.util.Collections
import javax.inject.Inject

// Модель представления активности
// используется для доступа к общим данным
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAttractions: GetAttractionsUseCase,
    private val attractionsRepository: AttractionsRepository,
    private val authRepository: AuthRepository,
    private val routesRepository: RoutesRepository
) : BaseViewModel() {
    // авторизован ли пользователь
    private val _isSignedIn = MutableLiveData<Boolean>()
    val isSignedIn = _isSignedIn.share()
    // событие запуска основного экрана
    private val _launchMainScreenEvent = MutableUnitLiveEvent()
    val launchMainScreenEvent = _launchMainScreenEvent.share()
    // словарь краткой информации о достопримечательности
    private val _attractionById =
        MutableLiveData<Map<Int, LiteAttraction>>()
    val attractionById = _attractionById.share()
    // текущий маршрут
    private val _currentRoute = MutableLiveData<Route>()
    val currentRoute = _currentRoute.share()
    // события начала/окончания оптимизации
    private val _optimizeEvent = MutableLiveEvent<Boolean>()
    val optimizeEvent = _optimizeEvent.share()
    // событие сохранения маршрута
    private val _saveRouteEvent = MutableUnitLiveEvent()
    val saveRouteEvent = _saveRouteEvent.share()
    // событие загрузки маршрута
    private val _loadRouteEvent = MutableUnitLiveEvent()
    val loadRouteEvent = _loadRouteEvent.share()
    // изменения статуса точки
    private val _pointStatusChangedEvent =
        MutableLiveEvent<Pair<Int, Boolean>>()
    val pointStatusChangedEvent = _pointStatusChangedEvent.share()

    init {
        viewModelScope.launch { authRepository.refresh() }
        isAuth()
    }

    // загрузка словаря краткой информации о достопримечательностях
    fun loadAttractions() = getAttractions(viewModelScope) {
        it.fold(::handleAttractionById, ::handleFailure)
    }
    // авторизован ли пользователь
    fun isAuth() = viewModelScope.launch {
        authRepository.isAuth().onSuccess {
            _isSignedIn.value = it
            if (!it) _currentRoute.value?.name = null
        }
    }
    // добавляет точку в маршрут
    fun addPointToRoute(id: Int) {
        _currentRoute.value?.pointIds?.add(id)
        _currentRoute.notifyObserver()
        _pointStatusChangedEvent.publishEvent(id to true)
    }
    // удаляет точку из маршрута
    fun removePointFromRoute(id: Int) {
        _currentRoute.value?.pointIds?.remove(id)
        _currentRoute.notifyObserver()
        _pointStatusChangedEvent.publishEvent(id to false)
    }
    // удаляет точку из маршрута в позиции index
    fun removePointFromRouteAt(index: Int) {
        val id = _currentRoute.value!!.pointIds[index]
        _currentRoute.value?.pointIds?.removeAt(index)
        _currentRoute.notifyObserver()
        _pointStatusChangedEvent.publishEvent(id to false)
    }
    // удаляет точку, если есть, иначе добавляет в маршрут
    fun removePointIfExistOtherwiseAdd(id: Int) {
        if (_currentRoute.value?.pointIds!!.contains(id)) {
            removePointFromRoute(id)
        } else {
            addPointToRoute(id)
        }
    }
    // меняет точки местами
    fun swapPointInRoute(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections
                    .swap(_currentRoute.value!!.pointIds, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections
                    .swap(_currentRoute.value!!.pointIds, i, i - 1)
            }
        }
        _currentRoute.notifyObserver()
    }
    // оптимизирует маршрут
    fun optimizeRoute(
        fixedFirst: Boolean,
        fixedLast: Boolean,
        userLocationPoint: Point?
    ) {
        _optimizeEvent.publishEvent(false)
        viewModelScope.launch {
            try {
                distanceMatrix(
                    attractionById.value!!,
                    currentRoute.value!!,
                    userLocationPoint
                ) {
                    removeGarbage(it, userLocationPoint != null)
                    viewModelScope.launch {
                        var userPaths: List<Int>? = null
                        if (userLocationPoint != null){
                            userPaths = it.first()
                            it.removeAt(0)
                        }
                        attractionsRepository.optimizeRoute(
                            OptimizationData(
                                fixFirst = fixedFirst,
                                fixLast = fixedLast,
                                ids = currentRoute.value!!.pointIds,
                                paths = it,
                                userPaths = userPaths,
                                userPosition = userLocationPoint != null
                            )
                        ).fold(::handleOptimization, ::handleFailure)
                    }
                }
            } catch (e : Failure) {
                handleFailure(e)
            }
        }
    }

    private fun removeGarbage(
        list: MutableList<MutableList<Int>>,
        useLocation: Boolean
    ) {
        for (i in useLocation.toInt() until list.size)
            list[i].removeAt(i - useLocation.toInt())
    }
    // обработка в случае успешной оптимизации
    private fun handleOptimization(ids: List<Int>) {
        _currentRoute.value!!.pointIds = ids.toMutableList()
        _optimizeEvent.publishEvent(true)
    }
    // установить флаг, что необходимо учитывать местоположение пользователя
    fun setUseLocation(useLocation: Boolean) {
        _currentRoute.value?.useLocation = useLocation
        _currentRoute.notifyObserver()
    }
    // установить тип маршрута
    fun setRouteType(type: Route.Type) {
        _currentRoute.value?.type = type
        _currentRoute.notifyObserver()
    }
    // обработка
    private fun handleAttractionById(
        attractionById: Map<Int, LiteAttraction>
    ) {
        _attractionById.value = attractionById
        _launchMainScreenEvent.publishEvent()
    }

    fun getRouteByName(name: String) = viewModelScope.launch {
        routesRepository.getRoute(name).fold(
            {
                _currentRoute.value = it
                _loadRouteEvent.publishEvent()
            },
            ::handleFailure
        )
    }

    fun saveRouteWithName(name: String) = viewModelScope.launch {
        val route = _currentRoute.value!!.copy(name = name)

        routesRepository.saveRoute(route).fold(
            {
                _currentRoute.value!!.name = name
                _saveRouteEvent.publishEvent()
            },
            ::handleFailure
        )
    }

    fun saveLocalRoute() = viewModelScope.launch {
        routesRepository.saveLocalRoute(currentRoute.value!!)
    }

    fun loadLocalRoute() = viewModelScope.launch {
        routesRepository.loadLocalRoute().fold(
            { _currentRoute.value = it },
            { _currentRoute.value = Route() }
        )
    }
}