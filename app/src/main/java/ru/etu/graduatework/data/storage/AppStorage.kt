package ru.etu.graduatework.data.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ru.etu.graduatework.di.dataStore
import ru.etu.graduatework.domain.model.Route
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Writer
import javax.inject.Inject

// класс, взаимодействующий с памятью устройства
class AppStorage @Inject constructor(
    private val appContext: Context
) {
    companion object {
        private val ACCESS_TOKEN_KEY =
            stringPreferencesKey("access_token")
        private val REFRESH_COOKIE_KEY =
            stringSetPreferencesKey("refresh_cookie")
        private val LAST_ROUTE_KEY = stringPreferencesKey("route")
    }
    // сохранить маршрут в памяти
    suspend fun saveLastRoute(route: Route) {
        appContext.dataStore.edit {
            it[LAST_ROUTE_KEY] = Json.encodeToString(
                Route.serializer(), route
            )
        }
    }
    // загрузить маршут из памяти
    suspend fun loadLastRoute(): Route {
        val routeJson = appContext.dataStore.data.map {
                it[LAST_ROUTE_KEY]
        }.first()

        return if (!routeJson.isNullOrEmpty()) {
            Json.decodeFromString(routeJson)
        } else {
            Route()
        }
    }
    // получить токен доступа
    suspend fun getAccessToken(): String? {
        return appContext.dataStore.data.map {
            it[ACCESS_TOKEN_KEY]
        }.first()
    }
    // получить данные cookie,
    // среди которых есть токен обновления
    suspend fun getRefreshCookie(): Set<String>? {
        return  appContext.dataStore.data.map {
            it[REFRESH_COOKIE_KEY]
        }.first()
    }
    // сохранить jwt токены
    suspend fun saveJWTData(
        accessToken: String,
        refreshCookie: Set<String>
    ) {
        appContext.dataStore.edit {
            it[ACCESS_TOKEN_KEY] = accessToken
            it[REFRESH_COOKIE_KEY] = refreshCookie
        }
    }
    // удалить jwt токены
    suspend fun deleteJWTData() {
        appContext.dataStore.edit {
            it.remove(ACCESS_TOKEN_KEY)
            it.remove(REFRESH_COOKIE_KEY)
        }
    }
}