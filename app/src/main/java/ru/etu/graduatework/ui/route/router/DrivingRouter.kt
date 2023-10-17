package ru.etu.graduatework.ui.route.router

import android.content.Context
import android.graphics.Color
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.runtime.Error

class DrivingRouter(routeCollection: MapObjectCollection, context: Context) :
    Router(routeCollection, context),
    DrivingSession.DrivingRouteListener {

    override fun requestInner(requestPoints: List<RequestPoint>) {
        val router = DirectionsFactory.getInstance().createDrivingRouter()
        router.requestRoutes(requestPoints, DrivingOptions(), VehicleOptions(), this)
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        var timeAndDistance: String = ""
        if (routes.isNotEmpty()) {
            val polylineMapObject = routeCollection.addPolyline(routes[0].geometry)
            polylineMapObject.setStrokeColor(Color.RED)
            val time = routes[0].metadata.weight.time.text
            val distance = routes[0].metadata.weight.distance.text
            timeAndDistance = "$time / $distance"
        }
        onSuccess?.invoke(timeAndDistance)
    }

    override fun onDrivingRoutesError(error: Error) = onError(error)
}