package ru.etu.graduatework.domain.repository

// интерфейс репозитория аутентификации
interface AuthRepository {
    // вход в аккаунт
    suspend fun signIn(
        username: String, password: String
    ): Result<Unit>
    // регистрация в аккаунта
    suspend fun signUp(
        email: String, username: String, password: String
    ): Result<Unit>
    // выход из аккаунта
    suspend fun logout(): Result<Unit>
    // авторизация аккаунта
    suspend fun isAuth(): Result<Boolean>
    // обновление jwt токенов
    suspend fun refresh(): Result<Unit>
}