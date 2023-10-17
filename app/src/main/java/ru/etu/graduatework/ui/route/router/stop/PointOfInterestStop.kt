package ru.etu.graduatework.ui.route.router.stop

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.yandex.mapkit.geometry.Point
import ru.etu.graduatework.R

class PointOfInterestStop(context: Context, point: Point) : Stop(context, point) {

    override val imgId = R.drawable.ic_pedestrian

    override val background = ResourcesCompat.getDrawable(context.resources, R.drawable.transport_background, null)
        ?.apply { setTint(Color.BLUE) }!!

    override fun descriptionTo(stop: UndergroundStop): SectionDescription {
        val text = "Идите пешком от текущей точки до станции «${stop.name}»"
        val views = listOf<View>(
            createImgView(),
            createArrowImageView(),
            stop.createImgView()
        )

        return SectionDescription(text, views, (point to stop.point))
    }

    override fun descriptionTo(stop: GroundStop): SectionDescription {
        val text = "Идите пешком от текущей точки до остановки «${stop.name}»"
        val views = listOf<View>(
            createImgView(),
            createArrowImageView(),
            stop.createImgView()
        )

        return SectionDescription(text, views, (point to stop.point))
    }

    override fun descriptionTo(stop: PointOfInterestStop): SectionDescription {
        val text = "Идите пешком от текущей точки до следующей"
        val views = listOf<View>(createImgView())

        return SectionDescription(text, views, (point to stop.point))
    }
}