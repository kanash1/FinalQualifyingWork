package ru.etu.graduatework.ui.route.router

import android.content.Context
import android.graphics.Color
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.SectionMetadata
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.runtime.Error
import java.text.NumberFormat
import java.util.Locale

class BicycleRouter(routeCollection: MapObjectCollection, context: Context) :
    Router(routeCollection, context),
    Session.RouteListener {

    override fun requestInner(requestPoints: List<RequestPoint>) {
        val router = TransportFactory.getInstance().createBicycleRouterV2()
        router.requestRoutes(requestPoints, TimeOptions(), this)
    }

    override fun onMasstransitRoutes(routes: MutableList<Route>) {
        var timeAndDistance: String = ""
        if (routes.isNotEmpty()) {
            for (section in routes[0].sections) {
                drawSection(
                    section.metadata.data, SubpolylineHelper.subpolyline(
                        routes[0].geometry, section.geometry
                    )
                )
            }
            val time = routes[0].metadata.weight.time.text
            val distance = routes[0].distanceBetweenPolylinePositions(
                routes[0].sections.first().geometry.begin,
                routes[0].sections.last().geometry.end
            )
            val strDistance =
                if (distance > 1000) "${String.format("%.1f", distance / 1000)} км"
                else "${String.format("%.1f", distance)} м"

            timeAndDistance = "$time / $strDistance"
        }
        onSuccess?.invoke(timeAndDistance)
    }

    private fun drawSection(data: SectionMetadata.SectionData, geometry: Polyline) {
        val polylineMapObject = routeCollection.addPolyline(geometry)
        if (data.transports == null) {
            polylineMapObject.setStrokeColor(Color.parseColor("#00e640"))
        }
    }

    override fun onMasstransitRoutesError(error: Error) = onError(error)
}