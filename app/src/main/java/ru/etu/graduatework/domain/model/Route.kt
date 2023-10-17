package ru.etu.graduatework.domain.model

import kotlinx.serialization.Serializable
import ru.etu.graduatework.data.network.RouteRequest

@Serializable
data class Route(
    var name: String? = null,
    var pointIds: MutableList<Int> = mutableListOf(),
    var type: Type = Type.PEDESTRIAN,
    var useLocation: Boolean = false,
    val firstFixed: Boolean = false,
    val lastFixed: Boolean = false
) {

    fun toRouteRequest() = RouteRequest(
        name = name!!,
        ids = pointIds,
        type = type.value,
        firstFixed = firstFixed,
        lastFixed = lastFixed
    )
    enum class Type(val value: String) {
        BICYCLE("bicycle"),
        DRIVING("auto"),
        MASSTRANSIT("masstransit"),
        PEDESTRIAN("pedestrian");

        companion object {
            fun fromString(value: String) = Type.values().first { it.value == value }
        }
    }
}


