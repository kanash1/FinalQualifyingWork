package ru.etu.graduatework.data.network.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.etu.graduatework.data.network.AppService
import ru.etu.graduatework.data.storage.AppStorage
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val appStorage: AppStorage,
    private val appService: AppService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val cookie = runBlocking {
            appStorage.getRefreshCookie()
        }
        return if (cookie != null) {
            runBlocking {
//                val newJWTDataResponse = appService.refresh(cookie)
                val newJWTDataResponse = appService.refresh()

                if (!newJWTDataResponse.isSuccessful || newJWTDataResponse.body() == null)
                    appStorage.deleteJWTData()

                val tokenResponse = newJWTDataResponse.body()
                val refreshCookie = newJWTDataResponse.headers().values("Set-Cookie").toSet()

                if (tokenResponse != null && refreshCookie.isNotEmpty()) {
                    tokenResponse.let {
                        appStorage.saveJWTData(tokenResponse.token, refreshCookie)
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${it.token}")
                            .build()
                    }
                } else
                    null
            }
        } else
            null
    }
}