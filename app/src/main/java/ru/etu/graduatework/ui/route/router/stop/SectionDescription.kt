package ru.etu.graduatework.ui.route.router.stop

import android.view.View
import com.yandex.mapkit.geometry.Point

data class SectionDescription(
    val text: String,
    val views: List<View>,
    val points: Pair<Point, Point>
)