package ru.etu.graduatework.domain.model

data class LiteAttraction(
    val id: Int,
    val name: String,
    val type: String?,
    val point: Point
)