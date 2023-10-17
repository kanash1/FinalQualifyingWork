package ru.etu.graduatework.domain

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.FilterVehicleTypes
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.mapkit.transport.masstransit.TransitOptions
import com.yandex.runtime.Error
import ru.etu.graduatework.core.exception.Failure
import ru.etu.graduatework.domain.model.LiteAttraction
import ru.etu.graduatework.domain.model.Route

fun distanceMatrix(
    attractionById: Map<Int, LiteAttraction>,
    route: Route,
    userLocationPoint: Point?,
    onResult: (list: MutableList<MutableList<Int>>) -> Unit) {

    val request = when (route.type) {
        Route.Type.BICYCLE -> { points: List<RequestPoint>, counter: Counter ->
            val router = TransportFactory.getInstance().createBicycleRouterV2()
            router.requestRoutes(points, TimeOptions(), counter)
            Unit
        }

        Route.Type.DRIVING -> { points: List<RequestPoint>, counter: Counter ->
            val router = DirectionsFactory.getInstance().createDrivingRouter()
            router.requestRoutes(points, DrivingOptions(), VehicleOptions(), counter)
            Unit
        }

        Route.Type.MASSTRANSIT -> { points: List<RequestPoint>, counter: Counter ->
            val router = TransportFactory.getInstance().createMasstransitRouter()
            router.requestRoutes(
                points,
                TransitOptions(FilterVehicleTypes.NONE.value, TimeOptions()),
                counter
            )
            Unit
        }

        Route.Type.PEDESTRIAN -> { points: List<RequestPoint>, counter: Counter ->
            val router = TransportFactory.getInstance().createPedestrianRouter()
            router.requestRoutes(points, TimeOptions(), counter)
            Unit
        }
    }

    val requestPoints = createListOfRequestPoints(attractionById, route.pointIds)
    val list: MutableList<MutableList<Int>> = MutableList(requestPoints.size) { mutableListOf() }
    var sublist: MutableList<Int> = MutableList(requestPoints.size) { Int.MAX_VALUE }
    var index = 0
    var subIndex = 0

    fun userLocationObserver(time: Int) {
        sublist[subIndex] = time
        ++subIndex
        if (subIndex >= requestPoints.size) {
            onResult(list)
        } else {
            Counter(::userLocationObserver).countDistance(
                RequestPoint(userLocationPoint!!, RequestPointType.WAYPOINT, null),
                requestPoints[subIndex],
                request
            )
        }
    }

    fun observer(time: Int) {
        sublist[subIndex] = time
        ++subIndex
        if (subIndex >= requestPoints.size) {
            list[index] = sublist
            sublist = MutableList(requestPoints.size) { Int.MAX_VALUE }
            subIndex = 0
            ++index
        }
        if (index >= requestPoints.size) {
            if (userLocationPoint == null) {
                onResult(list)
            } else {
                subIndex = 0
                sublist = MutableList(requestPoints.size) { Int.MAX_VALUE }
                list.add(0, sublist)
                Counter(::userLocationObserver).countDistance(
                    RequestPoint(userLocationPoint, RequestPointType.WAYPOINT, null),
                    requestPoints[subIndex],
                    request
                )
            }
        } else {
            Counter(::observer).countDistance(
                requestPoints[index],
                requestPoints[subIndex],
                request
            )
        }
    }

    Counter(::observer).countDistance(
        requestPoints[index],
        requestPoints[subIndex],
        request
    )
}

fun createListOfRequestPoints(
    attractionById: Map<Int, LiteAttraction>,
    pointIds: List<Int>
): List<RequestPoint> {
    val points = mutableListOf<RequestPoint>()
    for ((index, id) in pointIds.withIndex()) {
        val point = attractionById[id]!!.point.toMapPoint()
        if (index == 0 || index == pointIds.lastIndex)
            points.add(RequestPoint(point, RequestPointType.WAYPOINT, null))
        else
            points.add(RequestPoint(point, RequestPointType.VIAPOINT, null))
    }
    return points
}

class Counter(val observer: (time: Int) -> Unit) : Session.RouteListener,
    DrivingSession.DrivingRouteListener {
    fun countDistance(
        start: RequestPoint,
        destination: RequestPoint,
        request: (points: List<RequestPoint>, counter: Counter) -> Unit,
    ) {
        request(listOf(start, destination), this)
    }

    override fun onMasstransitRoutes(routes: MutableList<com.yandex.mapkit.transport.masstransit.Route>) {
        if (routes.isNotEmpty()) {
            observer(routes[0].metadata.weight.time.value.toInt())
        }
    }

    override fun onMasstransitRoutesError(error: Error) = throw Failure.NetWork

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        if (routes.isNotEmpty()) {
            observer(routes[0].metadata.weight.time.value.toInt())
        }
    }

    override fun onDrivingRoutesError(error: Error) = throw Failure.NetWork
}