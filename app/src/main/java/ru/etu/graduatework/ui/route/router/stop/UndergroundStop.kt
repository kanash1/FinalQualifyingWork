package ru.etu.graduatework.ui.route.router.stop

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.transport.masstransit.Line
import ru.etu.graduatework.R

class UndergroundStop(context: Context, val name: String, point: Point, private val line: Line) :
    Stop(context, point) {

    override val imgId = R.drawable.ic_spb_metro_logo

    override val background =
        ResourcesCompat.getDrawable(context.resources, R.drawable.transport_background, null)
            ?.apply {
                setTint(line.style!!.color!! or 0xFF000000.toInt())
            }!!

    override fun descriptionTo(stop: UndergroundStop): SectionDescription {
        val text: String
        val views = mutableListOf<View>()

        if (line.name != stop.line.name) {
            text = "Сделайте пересадку со станции «$name» на станцию «${stop.name}»"
            with(views) {
                add(createImgView())
                add(createArrowImageView())
                add(stop.createImgView())
            }
        } else {
            text = "От станции «$name» поезжайте до станции «${stop.name}»"
            views.add(createImgView())
        }

        return SectionDescription(text, views, (point to stop.point))
    }

    override fun descriptionTo(stop: GroundStop): SectionDescription {
        val text = "Идите пешком от станции «$name» до остановки «${stop.name}»"
        val views = listOf<View>(
            createImgView(),
            createArrowImageView(),
            stop.createImgView()
        )

        return SectionDescription(text, views, (point to stop.point))
    }

    override fun descriptionTo(stop: PointOfInterestStop): SectionDescription {
        val text = "Идите пешком от станции «$name» до точки интереса"
        val views = listOf<View>(
            createImgView(),
            createArrowImageView(),
            stop.createImgView()
        )

        return SectionDescription(text, views, (point to stop.point))
    }
}