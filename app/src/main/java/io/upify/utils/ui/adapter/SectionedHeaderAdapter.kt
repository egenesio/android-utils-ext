package io.upify.utils.ui.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

/**
 * Created by egenesio on 11/04/2018.
 */
class SectionedHeaderAdapter<T,R> (
        private val layouts: Triple<Int, Int, Int>,
        private val columns: Int): RecyclerView.Adapter<SectionedHeaderAdapter.ViewHolder>() {

    companion object {
        val TYPE_HEADER = 0
        val TYPE_SECTION = 1
        val TYPE_ITEM = 2
    }

    var list: List<Section<T, R>> by Delegates.observable(listOf()) { _, _, _ ->
        this.notifyDataSetChanged()
    }

    fun spanSizeLookup() = object : GridLayoutManager.SpanSizeLookup() {

        override fun getSpanSize(position: Int): Int = when {
            position == 0 -> columns
            list.getItemViewType(position - 1 ) == TYPE_SECTION -> columns
            else -> 1
        }
    }

    var listenerHeader: ((viewHolder: ViewHolder) -> Unit)? = null
    var listenerSection: ((viewHolder: ViewHolder, item: Section<T, R>) -> Unit)? = null
    var listenerItem: ((viewHolder: ViewHolder, sectionItem: Section<T, R>, item: R) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = when (viewType) {
            TYPE_HEADER -> layouts.first
            TYPE_SECTION -> layouts.second
            else -> layouts.third
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> TYPE_HEADER
        else -> list.getItemViewType(position - 1)
    }

    override fun getItemCount(): Int = list.getItemCount() + 1

    override fun onBindViewHolder(viewHolder: ViewHolder, aPosition: Int) {
        var position = aPosition - 1
        when {
            aPosition == 0 -> listenerHeader?.invoke(viewHolder)
            list.getItemViewType(position) == TYPE_SECTION -> {
                val sectionItem = list.getSectionItem(position)
                sectionItem?.let { listenerSection?.invoke(viewHolder, it) }
            }
            else -> {
                val item = list.getItem(position)
                item?.let { listenerItem?.invoke(viewHolder, it.first, it.second) }
            }
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}