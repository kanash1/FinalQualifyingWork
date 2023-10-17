package ru.etu.graduatework.core.exception

// базовый класс возможных ошибок программы
sealed class Failure : Throwable() {
    // ошибка сервера
    class Server(val code: Int): Failure()
    // ошибка сети
    object NetWork: Failure()
    // ошибка поиска
    object Search: Failure()
    // ошибка сериализации
    object Serialize : Failure()
    // ошибка получения местоположения
    object Location: Failure()
    // непредусмотренная программой ошибка
    object Unknown : Failure()
}
