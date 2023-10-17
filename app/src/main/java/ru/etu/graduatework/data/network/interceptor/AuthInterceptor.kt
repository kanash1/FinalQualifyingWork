package ru.etu.graduatework.data.network.interceptor

import android.util.Log
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import ru.etu.graduatework.data.network.TokenResponse
import ru.etu.graduatework.data.storage.AppStorage
import javax.inject.Inject

// аннотация для идентификации запросов
// требующих добавить в заголовок токен доступа
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthHeader

// аннотация для идентификации запросов
// требующих добавить в заголовок токен обновления
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CookieHeader

// класс-перехватчик запросов
class AuthInterceptor @Inject constructor(
    private val appStorage: AppStorage
) : Interceptor {
    // обрабатывает перехваченный запрос
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val method = request.tag(Invocation::class.java)!!.method()

        if (method.isAnnotationPresent(AuthHeader::class.java)) {
            val token = runBlocking {
                appStorage.getAccessToken()
            }
            return if (token != null) {
                val newRequest = request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                val response = chain.proceed(newRequest)
                if (response.code == 401) {
                    refresh(chain, request)
                } else {
                    response
                }
            } else {
                refresh(chain, request)
            }
        } else if (method.isAnnotationPresent(CookieHeader::class.java)) {
            val refreshCookie = runBlocking {
                appStorage.getRefreshCookie()
            }
            return if (refreshCookie != null) {
                val builder = request.newBuilder()
                refreshCookie.map { builder.addHeader("Cookie", it) }
                chain.proceed(builder.build())
            } else {
                chain.proceed(chain.request().newBuilder().build())
            }
        } else {
            return chain.proceed(chain.request().newBuilder().build())
        }
    }

    // запрашивает обновление токенов
    private fun refresh(
        chain: Interceptor.Chain,
        request: Request): Response
    {
        val cookie = runBlocking {
            appStorage.getRefreshCookie()
        }
        if (cookie != null) {
            return runBlocking {
                val builder = request.newBuilder()
                cookie.map { builder.addHeader("Cookie", it) }
                val newJWTDataResponse = chain.proceed(builder.build())

                if (!newJWTDataResponse.isSuccessful)
                    appStorage.deleteJWTData()

                try {
                    val tokenResponse =
                        Json.decodeFromString<TokenResponse?>(
                            newJWTDataResponse.body.string()
                        )
                    val refreshCookie = newJWTDataResponse
                        .headers.values("Set-Cookie").toSet()

                    if (tokenResponse != null &&
                        refreshCookie.isNotEmpty()) {
                        appStorage.saveJWTData(
                            tokenResponse.token,
                            refreshCookie
                        )
                        chain.proceed(
                            request.newBuilder()
                                .header(
                                    "Authorization",
                                    "Bearer ${tokenResponse.token}"
                                ).build()
                        )
                    } else
                        chain.proceed(request)
                } catch (e: Throwable) {
                    Log.e("ERROR", e.toString())
                    chain.proceed(request)
                }
            }
        } else
            return chain.proceed(request)
    }
}