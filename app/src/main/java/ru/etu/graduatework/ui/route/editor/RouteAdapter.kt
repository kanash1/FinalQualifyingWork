package ru.etu.graduatework.ui.route.editor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.etu.graduatework.R
import ru.etu.graduatework.domain.model.Attraction
import ru.etu.graduatework.domain.model.LiteAttraction
import ru.etu.graduatework.ui.MainViewModel
import java.util.Collections


@SuppressLint("NotifyDataSetChanged")
class RouteAdapter(private val viewModel: MainViewModel) :
    RecyclerView.Adapter<RouteAdapter.ViewHolder>(),
    ItemMoveCallback.ItemTouchHelperContract {

    private var points: MutableList<LiteAttraction> = mutableListOf()

    init {
        updateAll()
    }

    private lateinit var _listener: OnRemoveClickListener

    interface OnRemoveClickListener {
        fun onRemoveClick(position: Int)
    }

    fun updateAll() {
        points.clear()
        for (id in viewModel.currentRoute.value?.pointIds!!)
            points.add(viewModel.attractionById.value?.get(id)!!)
        notifyDataSetChanged()
    }

    fun setOnRemoveClickListener(listener: OnRemoveClickListener) {
        _listener = listener
    }

    fun removePointAt(index: Int) {
        viewModel.removePointFromRouteAt(index)
        points.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_edit, parent, false)
        return ViewHolder(itemView, _listener)
    }

    override fun getItemCount() = points.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = points[position].name
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(points, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(points, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        viewModel.swapPointInRoute(fromPosition, toPosition)
    }

    override fun onRowClear(holder: ViewHolder) {}

    override fun onRowSelected(holder: ViewHolder) {}

    class ViewHolder(itemView: View, listener: OnRemoveClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btn_remove)

        init {
            btnRemove.setOnClickListener {
                listener.onRemoveClick(bindingAdapterPosition)
            }
        }
    }
}