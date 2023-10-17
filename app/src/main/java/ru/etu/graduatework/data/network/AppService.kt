package ru.etu.graduatework.data.network

import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

// класс обертка для реализации интерфейса API
@Singleton
class AppService @Inject constructor(retrofit: Retrofit) : AppApi {
    // создание реализации интерфейса API
    private val appApi by lazy { retrofit.create(AppApi::class.java) }

    override suspend fun getLiteAttractions() =
        appApi.getLiteAttractions()

    override suspend fun optimizeRoute(request: OptimizationRequest) =
        appApi.optimizeRoute(request)

    override suspend fun getFullAttraction(attractionId: IdRequest) =
        appApi.getFullAttraction(attractionId)

    override suspend fun getSchedule(attractionId: IdRequest) =
        appApi.getSchedule(attractionId)

    override suspend fun getPicture(attractionId: IdRequest) =
        appApi.getPicture(attractionId)

    override suspend fun signUp(request: SignUpRequest) =
        appApi.signUp(request)

    override suspend fun signIn(request: SignInRequest) =
        appApi.signIn(request)

    override suspend fun refresh() = appApi.refresh()

    override suspend fun logout() = appApi.logout()

    override suspend fun saveRoute(request: RouteRequest) =
        appApi.saveRoute(request)

    override suspend fun getRoute(request: RouteNameRequest) =
        appApi.getRoute(request)

    override suspend fun getRoutes(): RouteNamesResponse =
        appApi.getRoutes()

    override suspend fun removeRoute(request: RouteNameRequest) =
        appApi.removeRoute(request)
}