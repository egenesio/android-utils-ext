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
fun<T,R> List<Section<T, R>>.getItemCount(): Int {
    return filter { it.isOpen }.fold(size) { acc, section ->
        acc + section.items.size
    }
}

fun<T,R> List<Section<T, R>>.getItemViewType(position: Int): Int {
    return if (getSectionItem(position) != null) SectionedListAdapter.TYPE_SECTION else SectionedListAdapter.TYPE_ITEM
}

fun<T,R> List<Section<T, R>>.getSectionItem(position: Int): Section<T, R>? {
    var count = 0
    forEach {
        if (position == count) return it
        count += 1
        if (it.isOpen) count += it.items.size
    }

    return null
}

fun<T,R> List<Section<T, R>>.getItem(position: Int): Pair<Section<T, R>, R>? {
    var count = 0
    forEach {
        count += 1

        if (it.isOpen && position >= count && position <= (count + it.items.size)) {
            it.items.forEachIndexed { index, item ->
                if (position == count + index) return Pair(it, item)
            }
        }

        if (it.isOpen) count += it.items.size
    }

    return null
}

class Section <out T, out R>(
        val sectionItem: T,
        val items: List<R>,
        var isOpen: Boolean)

class SectionedListAdapter<T,R> (
    private val sectionLayout: Int,
    private val itemLayout: Int,
    private val columns: Int = 1): RecyclerView.Adapter<SectionedListAdapter.ViewHolder>() {

    companion object {
        const val TYPE_SECTION = 1
        const val TYPE_ITEM = 2
    }

    var list: List<Section<T, R>> by Delegates.observable(listOf()) { _, _, _ ->
        this.notifyDataSetChanged()
    }

    fun spanSizeLookup() = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (list.getItemViewType(position) == TYPE_SECTION) columns else 1
        }
    }

    var listenerSection: ((viewHolder: ViewHolder, item: Section<T, R>) -> Unit)? = null
    var listenerItem: ((viewHolder: ViewHolder, sectionItem: Section<T, R>, item: R) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (viewType == TYPE_SECTION) sectionLayout else itemLayout

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = list.getItemViewType(position)

    override fun getItemCount(): Int = list.getItemCount()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (list.getItemViewType(position) == TYPE_SECTION){
            val sectionItem = list.getSectionItem(position)
            sectionItem?.let { listenerSection?.invoke(viewHolder, it) }
        } else {
            val item = list.getItem(position)
            item?.let { listenerItem?.invoke(viewHolder, it.first, it.second) }
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}