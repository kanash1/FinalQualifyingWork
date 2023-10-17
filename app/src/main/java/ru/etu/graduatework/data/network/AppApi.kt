package ru.etu.graduatework.data.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.etu.graduatework.data.network.interceptor.AuthHeader
import ru.etu.graduatework.data.network.interceptor.CookieHeader

// интерфейс API
interface AppApi {
    // запрос списка краткой информации о достопримечательностях
    @GET("api/lite_attractions_list")
    suspend fun getLiteAttractions(): LiteAttractionsResponse
    // запрос полной информации о достопримечательности с графиком работы
    @POST("api/full_attraction_info_with_schedule")
    suspend fun getFullAttraction(
        @Body attractionId: IdRequest
    ): FullAttractionResponse
    // запрос на получение маршрута
    @POST("api/get_route")
    @AuthHeader
    suspend fun getRoute(@Body request: RouteNameRequest): RouteResponse
    // запрос графика работы для достопримечательности
    @POST("api/schedule_by_id")
    suspend fun getSchedule(@Body attractionId: IdRequest): ScheduleResponse
    // запрос изображения достопримечательности
    @POST("api/picture_by_id")
    suspend fun getPicture(@Body attractionId: IdRequest): ResponseBody
    // запрос на вход в аккаунт
    @POST("auth/registration")
    suspend fun signUp(@Body request: SignUpRequest)
    // запрос на регистрацию
    @POST("auth/login")
    suspend fun signIn(@Body request: SignInRequest): Response<TokenResponse>
    // запрос на обновление токена доступа
    @GET("auth/refresh")
    @CookieHeader
    suspend fun refresh(): Response<TokenResponse>
    // запрос на выход из аккаунта
    @GET("auth/logout")
    @CookieHeader
    suspend fun logout()
    // запрос на сохранение маршрута
    @POST("api/save_route")
    @AuthHeader
    suspend fun saveRoute(@Body request: RouteRequest)
    // запрос на имен всех сохраненных маршрутов
    @GET("api/user_routes")
    @AuthHeader
    suspend fun getRoutes(): RouteNamesResponse
    // запрос на удаление маршрута
    @POST("api/remove_route")
    @AuthHeader
    suspend fun removeRoute(@Body request: RouteNameRequest)
    // запрос на оптимизацию
    @POST("api/optimize")
    suspend fun optimizeRoute(
        @Body request: OptimizationRequest
    ): OptimizationResponse
}