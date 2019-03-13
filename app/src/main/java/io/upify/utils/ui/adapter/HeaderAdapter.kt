package io.upify.utils.ui.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

/**
 * Created by egenesio on 11/04/2018.
 */
class HeaderAdapter<T> (private val layouts: Pair<Int, Int>, private val columns: Int = 1): androidx.recyclerview.widget.RecyclerView.Adapter<HeaderAdapter.ViewHolder>() {

    companion object {
        private val HEADER = 0
        private val ITEM = 1
    }

    var list: List<T> by Delegates.observable(listOf()) { _, _, _ ->
        this.notifyDataSetChanged()
    }

    var listenerHeader: ((viewHolder: ViewHolder) -> Unit)? = null
    var listenerItem: ((viewHolder: ViewHolder, item: T) -> Unit)? = null

    fun spanSizeLookup() = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (getItemViewType(position) == HEADER) columns else 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (viewType == HEADER) layouts.first else layouts.second

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = if (position == 0) HEADER else ITEM

    override fun getItemCount(): Int = list.size + 1

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (position == 0) listenerHeader?.invoke(viewHolder) else listenerItem?.invoke(viewHolder, list[position - 1])
    }

    class ViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}