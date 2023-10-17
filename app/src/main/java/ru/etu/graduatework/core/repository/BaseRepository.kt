package ru.etu.graduatework.core.repository

import android.util.Log
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import ru.etu.graduatework.core.exception.Failure
import java.io.IOException

// базовый класс для всех репозиториев
abstract class BaseRepository {
    // функция для обертки в результата в класс Result
    suspend fun <T> execute(block: suspend () -> T): Result<T> {
        return try {
            Result.success(wrapExceptions(block))
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
    // функция для обертки приходящих исключений
    // в понятные для прогрограммы исключения
    private suspend fun <T> wrapExceptions(block: suspend () -> T): T {
        var message = ""
        return try {
            block()
        // ошибки библиотеки kotlinx.serialization
        } catch (e: SerializationException) {
            e.message?.let { message = it }
            Log.e(SerializationException::class.simpleName, message)
            throw Failure.Serialize
        // ошибки библиотеки retrofit
        } catch (e: HttpException) {
            e.message?.let { message = it }
            Log.e(HttpException::class.simpleName, message)
            throw Failure.Server(e.code())
        // ошибки сети
        } catch (e: IOException) {
            e.message?.let { message = it }
            Log.e(IOException::class.simpleName, message)
            throw Failure.NetWork
        // непредусмотренные ошибки
        } catch (e : Throwable) {
            e.message?.let { message = it }
            Log.e(Throwable::class.simpleName, message)
            throw Failure.Unknown
        }
    }
}