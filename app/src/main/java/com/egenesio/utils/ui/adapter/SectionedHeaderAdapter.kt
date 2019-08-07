package com.egenesio.utils.ui.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

/**
 * Created by egenesio on 11/04/2018.
 */
class SectionedHeaderAdapter<T,R> (
    private val headerLayout: Int,
    private val sectionLayout: Int,
    private val itemLayout: Int,
    private val columns: Int = 1): RecyclerView.Adapter<SectionedHeaderAdapter.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_SECTION = 1
        const val TYPE_ITEM = 2
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
            TYPE_HEADER -> headerLayout
            TYPE_SECTION -> sectionLayout
            else -> itemLayout
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