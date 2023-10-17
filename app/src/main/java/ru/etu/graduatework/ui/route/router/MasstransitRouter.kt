// автор: Медведев О. В.

package ru.etu.graduatework.ui.route.router

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.FilterVehicleTypes
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.Section
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.mapkit.transport.masstransit.TransitOptions
import com.yandex.mapkit.transport.masstransit.Transport
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import ru.etu.graduatework.R
import ru.etu.graduatework.ui.route.router.stop.GroundStop
import ru.etu.graduatework.ui.route.router.stop.PointOfInterestStop
import ru.etu.graduatework.ui.route.router.stop.Stop
import ru.etu.graduatework.ui.route.router.stop.UndergroundStop

// класс для построения маршрута на общественном транспорте
class MasstransitRouter(
    routeCollection: MapObjectCollection,
    context: Context
) : Router(routeCollection, context), Session.RouteListener {
    // известные типы общественного транспорта
    private val vehicleTypes = hashSetOf<String>(
        "bus", "minibus", "trolleybus",
        "tramway", "underground", "railway"
    )
    // остановки маршрута, которые
    // используются для построения описания
    private val stopsForDescription = mutableListOf<Stop>()
    // была ли предыдущая секция маршрута пешеходной
    private var isLastSectionPedestrian: Boolean? = null
    // запрос на построение маршрута на сервер Яндекса
    override fun requestInner(requestPoints: List<RequestPoint>) {
        stopsForDescription.clear()
        isLastSectionPedestrian = null
        val router = TransportFactory
            .getInstance().createMasstransitRouter()
        router.requestRoutes(
            requestPoints,
            TransitOptions(
                FilterVehicleTypes.NONE.value,
                TimeOptions()
            ),
            this
        )
    }
    // метод обратного вызова при окончании построения маршрута
    override fun onMasstransitRoutes(routes: MutableList<Route>) {
        var timeAndDistance: String = ""
        if (routes.isNotEmpty()) {
            for (section in routes[0].sections) {
                drawSection(
                    section,
                    SubpolylineHelper.subpolyline(
                        routes[0].geometry, section.geometry
                    )
                )
            }
            stopsForDescription.add(PointOfInterestStop(
                context,
                SubpolylineHelper.subpolyline(
                    routes[0].geometry,
                    routes[0].sections.last().geometry
                ).points.last()
            ))
            val time = routes[0].metadata.weight.time.text
            val distance = routes[0]
                .distanceBetweenPolylinePositions(
                    routes[0].sections.first().geometry.begin,
                    routes[0].sections.last().geometry.end
                )
            val strDistance =
                if (distance > 1000)
                    "${String.format("%.1f", distance / 1000)} км"
                else
                    "${String.format("%.1f", distance)} м"

            timeAndDistance = "$time / $strDistance"
        }
        onSuccess?.invoke(timeAndDistance)
    }

    fun getStops(): List<Stop> {
        return stopsForDescription
    }

    // метод обратного вызова при ошибке
    override fun onMasstransitRoutesError(
        error: Error
    ) = onError(error)
    // отрисовка секции на карте
    private fun drawSection(section: Section, geometry: Polyline) {
        // добавление полилинии на карту
        val polylineMapObject =
            routeCollection.addPolyline(geometry)
        val data = section.metadata.data
        val stops = section.stops
        // если секция не пешеходная
        if (!data.transports.isNullOrEmpty()) {
            val transport = data.transports!!.first()
            // добавление меток остановок секции маршрута
            val starStopMapObject =
                routeCollection.addPlacemark(stops.first().position)
            val endStopMapObject =
                routeCollection.addPlacemark(stops.last().position)

            // если транспортная линия имеет стиль, то это метро
            if (transport.line.style != null) {
                val color =
                    transport.line.style!!.color!! or 0xFF000000.toInt()
                polylineMapObject.setStrokeColor(color)
                polylineMapObject.outlineColor = Color.BLACK
                polylineMapObject.outlineWidth = 2f
                starStopMapObject
                    .setIcon(getImageProviderFromDrawable(color))
                endStopMapObject
                    .setIcon(getImageProviderFromDrawable(color))

                with(stopsForDescription) {
                    add(UndergroundStop(
                        context,
                        stops.first().metadata.stop.name,
                        stops.first().position,
                        transport.line
                    ))
                    add(UndergroundStop(
                        context,
                        stops.last().metadata.stop.name,
                        stops.last().position,
                        transport.line
                    ))
                }
            } else {
                // другой транспорт
                val sectionVehicleType = getVehicleType(transport)
                if (vehicleTypes.contains(sectionVehicleType)) {
                    polylineMapObject.setStrokeColor(Color.GREEN)
                    polylineMapObject.outlineColor = Color.BLACK
                    polylineMapObject.outlineWidth = 2f
                    starStopMapObject
                        .setIcon(getImageProviderFromDrawable(Color.GREEN))
                    endStopMapObject
                        .setIcon(getImageProviderFromDrawable(Color.GREEN))
                    with(stopsForDescription) {
                        add(GroundStop(
                            context,
                            stops.first().metadata.stop.name,
                            stops.first().position,
                            data.transports
                        ))
                        add(GroundStop(
                            context,
                            stops.last().metadata.stop.name,
                            stops.last().position
                        ))
                    }
                }
            }
            isLastSectionPedestrian = false
        } else {
            // пешеходная секция
            polylineMapObject.dashLength = 20f
            polylineMapObject.gapLength = 10f
            polylineMapObject.setStrokeColor(Color.BLUE)

            // для отрисовки пешеходных линий может потребоваться
            // несколько подряд идущих секций маршрута,
            // но атрибут fitness не будет null, только на первом
            // таком участке, поэтому эти проверки необходимы для
            // фиксации момента достижения точки интереса.
            if (data.fitness != null) {
                if (isLastSectionPedestrian == null ||
                    isLastSectionPedestrian!!)
                    stopsForDescription
                        .add(PointOfInterestStop(
                            context, geometry.points.first())
                        )
                isLastSectionPedestrian = true
            }
        }
    }
    // преобразует изобрадежние в формате png, jpg и т.д.
    // в формат используемый MapKit
    private fun getImageProviderFromDrawable(
        color: Int
    ): ImageProvider {
        val drawable = ContextCompat
            .getDrawable(context, R.drawable.stop) as GradientDrawable
        drawable.setColor(color)
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return ImageProvider.fromBitmap(bitmap)
    }
    // если названии есть среди известных типов транспорта
    // (MapKit не предоставляет этот список),
    // то возвращаем его тип
    private fun getVehicleType(transport: Transport): String? {
        for (type in transport.line.vehicleTypes) {
            if (vehicleTypes.contains(type)) {
                return type
            }
        }
        return null
    }
}