package ru.etu.graduatework.domain.model

import com.yandex.mapkit.map.CameraPosition

data class OptimizationData(
    val fixFirst: Boolean,
    val fixLast: Boolean,
    val ids: List<Int>,
    val paths: List<List<Int>>,
    val userPaths: List<Int>? = null,
    val userPosition: Boolean = false
)
