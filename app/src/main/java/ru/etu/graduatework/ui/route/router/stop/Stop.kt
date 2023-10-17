package ru.etu.graduatework.ui.route.router.stop

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.yandex.mapkit.geometry.Point
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.dpToPx

sealed class Stop(protected val context: Context, val point: Point) {
    protected abstract val imgId: Int
    protected abstract val background: Drawable


    fun descriptionTo(stop: Stop): SectionDescription {
        return when (stop) {
            is UndergroundStop -> descriptionTo(stop)
            is GroundStop -> descriptionTo(stop)
            is PointOfInterestStop -> descriptionTo(stop)
        }
    }

    fun createArrowImageView(): ImageView {
        return ImageView(context).apply {
            minimumHeight = 24f.dpToPx()
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setImageResource(R.drawable.ic_arrow_right)
            setPadding(8f.dpToPx(), 0, 8f.dpToPx(), 0)
        }
    }

    fun createImgView(): ImageView {
        val background = background
        return ImageView(context).apply {
            minimumHeight = 24f.dpToPx()
            this.background = background
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(8f.dpToPx(), 0, 8f.dpToPx(), 0)
            setImageResource(imgId)
        }
    }

    fun createTextView(text: String, withDrawable: Boolean = false): TextView {
        val background = background
        return TextView(context).apply {
            minimumHeight = 24f.dpToPx()
            this.text = text
            this.background = background
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            textSize = 16f
            setPadding(8f.dpToPx(), 0, 8f.dpToPx(), 0)
            setTextColor(Color.WHITE)
            if (withDrawable) {
                compoundDrawablePadding = 12
                setCompoundDrawablesWithIntrinsicBounds(imgId, 0, 0, 0)
            }
        }
    }

    protected abstract fun descriptionTo(stop: UndergroundStop): SectionDescription
    protected abstract fun descriptionTo(stop: GroundStop): SectionDescription
    protected abstract fun descriptionTo(stop: PointOfInterestStop): SectionDescription
}
