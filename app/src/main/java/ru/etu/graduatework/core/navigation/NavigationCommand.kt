package ru.etu.graduatework.core.navigation

import androidx.navigation.NavDirections

// базовый класс для команд навигации
sealed class NavigationCommand {
    // класс команды для навигации
    // к заданному фрагменту
    data class ToDirection(
        val directions: NavDirections
    ): NavigationCommand()
    // класс команды для возвращения к предыдущему
    // фрагементу
    object Back : NavigationCommand()
}
