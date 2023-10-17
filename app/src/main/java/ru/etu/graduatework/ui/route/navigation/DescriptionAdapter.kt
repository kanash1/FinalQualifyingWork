package ru.etu.graduatework.ui.route.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.directions.driving.Description
import com.yandex.mapkit.geometry.Point
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.setMarginsInDp
import ru.etu.graduatework.ui.map.MapFragment
import ru.etu.graduatework.ui.route.router.stop.SectionDescription
import ru.etu.graduatework.ui.route.router.stop.Stop

class DescriptionAdapter(private val stops: List<Stop>) :
    RecyclerView.Adapter<DescriptionAdapter.ViewHolder>() {

    val pointPairs: List<Pair<Point, Point>> = mutableListOf<Pair<Point, Point>>().apply {
        for (position in 0 until stops.lastIndex)
            add((stops[position].point to stops[position + 1].point))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_masstransit_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = stops.lastIndex

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sectionDescription = stops[position].descriptionTo(stops[position + 1])
        holder.setIsRecyclable(false)
        holder.tvDescription.text = sectionDescription.text
        for ((i, view) in sectionDescription.views.withIndex()) {
            holder.llIcons.addView(view)
            if (i == 0) view.setMarginsInDp(8f)
            else view.setMarginsInDp(0f, 8f, 8f, 8f)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.llIcons.removeAllViews()
        super.onViewRecycled(holder)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llIcons: LinearLayout
        val tvDescription: TextView

        init {
            llIcons = itemView.findViewById(R.id.ll_icons)
            tvDescription = itemView.findViewById(R.id.tv_description)
        }
    }
}