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
class HeaderFooterAdapter<T> (private val layouts: Triple<Int, Int, Int>, private val columns: Int = 1):
        androidx.recyclerview.widget.RecyclerView.Adapter<HeaderFooterAdapter.ViewHolder>() {

    companion object {
        private val HEADER = 0
        private val ITEM = 1
        private val FOOTER = 2
    }

    var list: List<T> by Delegates.observable(listOf()) { _, _, _ ->
        this.notifyDataSetChanged()
    }

    var listenerHeader: ((viewHolder: ViewHolder) -> Unit)? = null
    var listenerItem: ((viewHolder: ViewHolder, item: T) -> Unit)? = null
    var listenerFooter: ((viewHolder: ViewHolder) -> Unit)? = null

    fun spanSizeLookup() = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (getItemViewType(position) == HEADER || getItemViewType(position) == FOOTER) columns else 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = when(viewType) {
            HEADER -> layouts.first
            FOOTER -> layouts.third
            else -> layouts.second
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = when(position){
        0 -> HEADER
        itemCount -1 -> FOOTER
        else -> ITEM
    }

    override fun getItemCount(): Int = list.size + 2

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        when(position){
            0 -> listenerHeader?.invoke(viewHolder)
            itemCount -1 -> listenerFooter?.invoke(viewHolder)
            else -> listenerItem?.invoke(viewHolder, list[position - 1])
        }
    }

    class ViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}