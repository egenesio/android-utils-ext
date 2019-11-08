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
class SectionedFooterAdapter<T,R> (
    private val footerLayout: Int,
    private val sectionLayout: Int,
    private val itemLayout: Int,
    private val columns: Int = 1): RecyclerView.Adapter<SectionedFooterAdapter.ViewHolder>() {

    companion object {
        const val TYPE_FOOTER = 0
        const val TYPE_SECTION = 1
        const val TYPE_ITEM = 2
    }

    var list: List<Section<T, R>> by Delegates.observable(listOf()) { _, _, _ ->
        this.notifyDataSetChanged()
    }

    fun spanSizeLookup() = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int = when {
            position == itemCount - 1  -> columns
            list.getItemViewType(position - 1 ) == TYPE_SECTION -> columns
            else -> 1
        }
    }

    var listenerFooter: ((viewHolder: ViewHolder) -> Unit)? = null
    var listenerSection: ((viewHolder: ViewHolder, item: Section<T, R>, position: Int) -> Unit)? = null
    var listenerItem: ((viewHolder: ViewHolder, sectionItem: Section<T, R>, item: R, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = when (viewType) {
            TYPE_FOOTER -> footerLayout
            TYPE_SECTION -> sectionLayout
            else -> itemLayout
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        itemCount - 1 -> TYPE_FOOTER
        else -> list.getItemViewType(position)
    }

    override fun getItemCount(): Int = list.getItemCount() + 1

    override fun onBindViewHolder(viewHolder: ViewHolder, aPosition: Int) {
        var position = aPosition - 1
        when {
            aPosition == itemCount -1 -> listenerFooter?.invoke(viewHolder)
            list.getItemViewType(aPosition) == TYPE_SECTION -> {
                val sectionItem = list.getSectionItem(aPosition)
                sectionItem?.let { listenerSection?.invoke(viewHolder, it, aPosition) }
            }
            else -> {
                val item = list.getItem(aPosition)
                item?.let { listenerItem?.invoke(viewHolder, it.first, it.second, aPosition) }
            }
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}