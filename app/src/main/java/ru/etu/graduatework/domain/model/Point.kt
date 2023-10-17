package ru.etu.graduatework.domain.model

import com.yandex.mapkit.geometry.Point as MapPoint

data class Point(
    val latitude: Double,
    val longitude: Double
) {
    fun toMapPoint(): MapPoint {
        return MapPoint(latitude, longitude)
    }
}

