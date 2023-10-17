package ru.etu.graduatework.domain.repository

import ru.etu.graduatework.domain.model.Route

// интерфейс репозитория маршрутов
interface RoutesRepository {
    // получить маршрут с сервера
    suspend fun getRoute(name: String): Result<Route>
    // получить имена маршрутов с сервера
    suspend fun getRoutes(): Result<List<String>>
    // сохранить маршрут на сервере
    suspend fun saveRoute(route: Route): Result<Unit>
    // удалить маршрут
    suspend fun removeRoute(name: String): Result<Unit>
    // сохранить маршрут в памяти устройства
    suspend fun saveLocalRoute(route: Route): Result<Unit>
    // загрузить маршрут из памяти устройства
    suspend fun loadLocalRoute(): Result<Route>
}