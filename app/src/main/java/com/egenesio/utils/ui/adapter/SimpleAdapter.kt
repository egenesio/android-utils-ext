package com.egenesio.utils.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

/**
 * Created by egenesio on 11/04/2018.
 */
class SimpleAdapter<T> (private val layoutsMap: Map<Int, Int>?): RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {

    private var layout: Int? = null

    constructor(layout: Int): this(null){
        this.layout = layout
    }

    var list: List<T> by Delegates.observable(listOf()) { _, _, _ ->
        this.notifyDataSetChanged()
    }

    var listener: ((viewHolder: ViewHolder, item: T, position: Int) -> Unit)? = null
    var onGetItemViewType: ((item: T) -> Int)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = layout ?: layoutsMap?.get(viewType) ?: 0

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = onGetItemViewType?.invoke(list[position]) ?: super.getItemViewType(position)

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        listener?.invoke(viewHolder, list[position], position)

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}