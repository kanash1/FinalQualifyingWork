// автор: Медведев О. В.
// данный файл содержит все классы, объекты которых
// отпраляются в запросах или принимаются в ответах

package ru.etu.graduatework.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.etu.graduatework.domain.model.Attraction
import ru.etu.graduatework.domain.model.LiteAttraction
import ru.etu.graduatework.domain.model.Point
import ru.etu.graduatework.domain.model.Route
import ru.etu.graduatework.domain.model.Schedule

// полная информация о достопримечательности
@Serializable
data class FullAttractionResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("class") val type: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("coord_longitude") val longitude: Double,
    @SerialName("coord_latitude") val latitude: Double,
    @SerialName("rating") val rating: Double? = null,
    @SerialName("cost") val cost: Int? = null,
    @SerialName("working") val isWorking: Boolean,
    @SerialName("schedule")
    val schedule: List<ScheduleItemResponse> = emptyList(),
    @SerialName("today_schedule")
    val today: TodayScheduleItemResponse,
) {
    fun toLiteAttractionResponse() =
        LiteAttractionResponse(
            id, name, type, longitude, latitude, isWorking
        )

    fun toAttraction(): Attraction {
        val scheduleItems = schedule.map { it.toScheduleItem() }
        return Attraction(
            id,
            name,
            type,
            rating,
            Schedule(today.toTodayScheduleItem(), scheduleItems),
            cost,
            description,
            Point(longitude, latitude),
            isWorking
        )
    }
}

@Serializable
data class IdRequest(
    @SerialName("id") val value: Int
)

// краткая информация о достопримечательности
@Serializable
data class LiteAttractionResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("class") val type: String? = null,
    @SerialName("coord_longitude") val longitude: Double,
    @SerialName("coord_latitude") val latitude: Double,
    @SerialName("working") val isWorking: Boolean
) {
    fun toLiteAttraction() =
        LiteAttraction(id, name, type, Point(longitude, latitude))
}

// список краткой информации о достопримечательностях
@Serializable
data class LiteAttractionsResponse(
    @SerialName("attractions")
    val value: List<LiteAttractionResponse>
)

// данные для оптимизации о достопримечательностях
@Serializable
data class OptimizationRequest(
    @SerialName("userPaths") val userPaths: List<Int>? = null,
    @SerialName("userPosition") val userPosition: Boolean = false,
    @SerialName("id") val ids: List<Int>,
    @SerialName("paths") val paths: List<List<Int>>,
    @SerialName("firstFixed") val fixFirst: Boolean,
    @SerialName("lastFixed") val fixLast: Boolean
)

// точки оптимизированного маршрута
//@Serializable
//data class OptimizationResponse(
//    @SerialName("ids") val ids: List<Int>
//)

typealias OptimizationResponse = List<Int>

@Serializable
data class RouteNameRequest(
    @SerialName("name") val value: String
)

typealias RouteNameResponse = RouteNameRequest

// список имен сохраненных маршрутов
@Serializable
data class RouteNamesResponse(
    @SerialName("savedRoutes")
    val value: List<RouteNameResponse>
)

// данные маршрута
@Serializable
data class RouteRequest(
    @SerialName("name") val name: String,
    @SerialName("points") val ids: List<Int>,
    @SerialName("firstFixed") val firstFixed: Boolean,
    @SerialName("lastFixed") val lastFixed: Boolean,
    @SerialName("way") val type: String
) {
    fun toRoute() = Route(
        name = name,
        pointIds = ids.toMutableList(),
        firstFixed = firstFixed,
        lastFixed = lastFixed,
        type = Route.Type.fromString(type)
    )
}

typealias RouteResponse = RouteRequest

// график работы
@Serializable
data class ScheduleResponse(
    @SerialName("schedule")
    val value: List<ScheduleItemResponse>
)

@Serializable
data class SignInRequest(
    @SerialName("login") val username: String,
    @SerialName("password") val password: String
)

@Serializable
data class SignUpRequest(
    @SerialName("email") val email: String,
    @SerialName("login") val username: String,
    @SerialName("password") val password: String
)

// токен доступа
@Serializable
data class TokenResponse(
    @SerialName("access") val token: String,
    @SerialName("user") val user: User? = null
) {
    @Serializable
    data class User(
        @SerialName("login") val username: String,
        @SerialName("day") val day: String,
        @SerialName("time") val time: String
    )
}

// элемент рабочего графика
@Serializable
data class ScheduleItemResponse(
    @SerialName("day_of_the_week") val day: Int,
    @SerialName("work_start") val start: String,
    @SerialName("work_stop") val stop: String,
) {
    fun toScheduleItem(): Schedule.Item {
        val day = (day - 1) % 7
        return Schedule.Item(
            Schedule.DayOfWeek.fromInt(day), start, stop
        )
    }
}

// график работы на сегодяншний день
@Serializable
data class TodayScheduleItemResponse(
    @SerialName("day_of_the_week") val day: Int,
    @SerialName("work_start") val start: String? = null,
    @SerialName("work_stop") val stop: String? = null,
    @SerialName("is_working") val isWorking: Boolean
) {
    fun toTodayScheduleItem() : Schedule.TodayItem {
        val day = (day - 1) % 7
        return Schedule.TodayItem(
            Schedule.DayOfWeek.fromInt(day), start, stop, isWorking
        )
    }
}