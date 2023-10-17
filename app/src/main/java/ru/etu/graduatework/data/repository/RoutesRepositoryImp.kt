package ru.etu.graduatework.data.repository

import ru.etu.graduatework.core.repository.BaseRepository
import ru.etu.graduatework.data.network.AppService
import ru.etu.graduatework.data.network.RouteNameRequest
import ru.etu.graduatework.data.network.StubService
import ru.etu.graduatework.data.storage.AppStorage
import ru.etu.graduatework.domain.model.Route
import ru.etu.graduatework.domain.repository.RoutesRepository
import javax.inject.Inject

// реализация интерфейса репозитория маршрутов
class RoutesRepositoryImp @Inject constructor(
    private val service: AppService,
    private val storage: AppStorage
) : BaseRepository(), RoutesRepository {
    override suspend fun getRoute(name: String) = execute {
        service.getRoute(RouteNameRequest(name)).toRoute()
    }

    override suspend fun getRoutes() = execute {
        service.getRoutes().value.map { it.value }
    }

    override suspend fun saveRoute(route: Route) = execute {
        service.saveRoute(route.toRouteRequest())
    }

    override suspend fun removeRoute(name: String) = execute {
        service.removeRoute(RouteNameRequest(name))
    }

    override suspend fun saveLocalRoute(route: Route) = execute {
        storage.saveLastRoute(route)
    }

    override suspend fun loadLocalRoute() = execute {
        storage.loadLastRoute()
    }
}