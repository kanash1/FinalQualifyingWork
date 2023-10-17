package ru.etu.graduatework.ui.map

import android.content.Context
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectVisitor
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.runtime.image.ImageProvider
import ru.etu.graduatework.R

class Visitor(
    private val ids: List<Int>,
    private val context: Context
) : MapObjectVisitor {
    override fun onPlacemarkVisited(
        mapObject: PlacemarkMapObject
    ) {
        val id = mapObject.userData as Int

    }

    override fun onPolylineVisited(
        mapObject: PolylineMapObject
    ) {}

    override fun onPolygonVisited(
        mapObject: PolygonMapObject
    ) {}

    override fun onCircleVisited(
        mapObject: CircleMapObject
    ) {}

    override fun onCollectionVisitStart(
        mapObject: MapObjectCollection
    ): Boolean { return false }

    override fun onCollectionVisitEnd(
        mapObject: MapObjectCollection
    ) {}

    override fun onClusterizedCollectionVisitStart(
        mapObject: ClusterizedPlacemarkCollection
    ): Boolean { return true }

    override fun onClusterizedCollectionVisitEnd(
        mapObject: ClusterizedPlacemarkCollection
    ) {}
}