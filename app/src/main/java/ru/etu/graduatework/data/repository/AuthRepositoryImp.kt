package ru.etu.graduatework.data.repository

import kotlinx.coroutines.runBlocking
import ru.etu.graduatework.core.repository.BaseRepository
import ru.etu.graduatework.data.network.AppService
import ru.etu.graduatework.data.network.SignInRequest
import ru.etu.graduatework.data.network.SignUpRequest
import ru.etu.graduatework.data.network.StubService
import ru.etu.graduatework.data.storage.AppStorage
import ru.etu.graduatework.domain.repository.AuthRepository
import javax.inject.Inject

// реализация интерфейса репозитория аутентификации
class AuthRepositoryImp @Inject constructor(
    private val service: AppService,
    private val storage: AppStorage
) : BaseRepository(), AuthRepository {
    override suspend fun signIn(
        username: String, password: String
    ) = execute {
        val response = service
            .signIn(SignInRequest(username, password))
        val accessToken = response.body()?.token
        val refreshToken = response
            .headers().values("Set-Cookie").toSet()
        storage.saveJWTData(accessToken!!, refreshToken)
    }

    override suspend fun signUp(
        email: String, username: String, password: String
    ) = execute {
        service.signUp(SignUpRequest(email, username, password))
    }

    override suspend fun logout() = execute {
        service.logout()
        storage.deleteJWTData()
    }

    override suspend fun isAuth() = execute {
        val accessToken = runBlocking { storage.getAccessToken() }
        return@execute accessToken != null
    }

    override suspend fun refresh() = execute {
        val tokenResponse = service.refresh()
        val accessToken = tokenResponse.body()?.token
        val refreshToken = tokenResponse
            .headers().values("Set-Cookie").toSet()
        storage.saveJWTData(accessToken!!, refreshToken)
    }
}