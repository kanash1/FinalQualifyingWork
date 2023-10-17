package ru.etu.graduatework.ui.route.load

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.etu.graduatework.R

class LoadSavedRoutesAdapter(private var routes: MutableList<String> = mutableListOf()) :
    RecyclerView.Adapter<LoadSavedRoutesAdapter.ViewHolder>() {

    var isEnabled = true

    private lateinit var _itemListener: OnClickListener
    private lateinit var _btnListener: OnClickListener

    interface OnClickListener {
        fun onClick(position: Int)
    }

    fun OnClickListener.onClickSafe(position: Int) {
        if (isEnabled)
            onClick(position)
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        _itemListener = listener
    }

    fun setOnDeleteClickListener(listener: OnClickListener) {
        _btnListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAll(newRoutes: MutableList<String>) {
        routes = newRoutes
        notifyDataSetChanged()
    }

    fun removePointAt(index: Int) {
        routes.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_saved_route, parent, false)
        return ViewHolder(itemView, _itemListener, _btnListener)
    }

    override fun getItemCount() = routes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = routes[position]
    }

    inner class ViewHolder(
        itemView: View,
        itemListener: OnClickListener,
        deleteListener: OnClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        init {
            tvName.setOnClickListener {
                itemListener.onClickSafe(bindingAdapterPosition)
            }
            btnDelete.setOnClickListener {
                deleteListener.onClickSafe(bindingAdapterPosition)
            }
        }
    }
}