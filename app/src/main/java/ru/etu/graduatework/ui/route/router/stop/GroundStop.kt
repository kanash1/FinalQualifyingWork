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
import com.yandex.mapkit.transport.masstransit.Transport
import ru.etu.graduatework.R

class GroundStop(
    context: Context,
    val name: String,
    point: Point,
    private val transports: List<Transport>? = null
) : Stop(context, point) {

    override val imgId = R.drawable.ic_masstransit

    override val background =
        ResourcesCompat.getDrawable(context.resources, R.drawable.transport_background, null)
            ?.apply {
                setTint(Color.parseColor("#00e640"))
            }!!

    override fun descriptionTo(stop: UndergroundStop): SectionDescription {
        val text = "Идите пешком от остановки «$name» до станции «${stop.name}»"
        val views = listOf<View>(
            createArrowImageView(),
            stop.createImgView()
        )
        return SectionDescription(text, views, (point to stop.point))
    }

    override fun descriptionTo(stop: GroundStop): SectionDescription {
        val text: String
        val views = mutableListOf<View>()

        if (name == stop.name) {
            text = "Сделайте пересадку на остановке «$name»"
            views.add(createImgView())
        } else if (transports != null && stop.transports == null) {
            text = "Поезжайте от остановки «$name» до остановки «${stop.name}»"
            fillViewList(views)
        } else {
            text = "Идите пешком от остановки «$name» до остановки «${stop.name}»"
            with(views) {
                add(createImgView())
                add(createArrowImageView())
                add(stop.createImgView())
            }
        }

        return SectionDescription(text, views, (point to stop.point))
    }

    private fun fillViewList(views: MutableList<View>) {
        var withDrawable: Boolean = true
        with(views) {
            if (!transports.isNullOrEmpty()) {
                for ((i, transport) in transports.withIndex()) {
                    val text = transport.line.name
                    add(createTextView(text, withDrawable))
                    if (i == 0) withDrawable = false
                }
            }
        }
    }

    override fun descriptionTo(stop: PointOfInterestStop): SectionDescription {
        val text = "Идите пешком от остановки «$name» до точки интереса"
        val views = listOf<View>(stop.createImgView())

        return SectionDescription(text, views, (point to stop.point))
    }
}