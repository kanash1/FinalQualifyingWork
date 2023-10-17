package ru.etu.graduatework.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.etu.graduatework.R
import ru.etu.graduatework.domain.model.LiteAttraction

class SearchAdapter(results: List<LiteAttraction> = mutableListOf()):
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var results: List<LiteAttraction> = results
        @SuppressLint("NotifyDataSetChanged")
        set(results) {
            field = results
            notifyDataSetChanged()
        }

    private lateinit var _listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        _listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_search, parent, false)
        return ViewHolder(itemView, _listener)
    }

    override fun getItemCount() = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvResult.text = results[position].name
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        val tvResult: TextView = itemView.findViewById(R.id.tv_result)

        init {
            tvResult.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }
}