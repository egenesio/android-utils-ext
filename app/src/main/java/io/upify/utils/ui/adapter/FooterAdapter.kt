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
class FooterAdapter<T> (
    private val footerLayout: Int,
    private val itemLayout: Int,
    private val columns: Int = 1): RecyclerView.Adapter<FooterAdapter.ViewHolder>() {

    companion object {
        private const val FOOTER = 0
        private const val ITEM = 1
    }

    var list: List<T> by Delegates.observable(listOf()) { _, _, _ ->
        this.notifyDataSetChanged()
    }

    var listenerfooter: ((viewHolder: ViewHolder) -> Unit)? = null
    var listenerItem: ((viewHolder: ViewHolder, item: T, position: Int) -> Unit)? = null

    fun spanSizeLookup() = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (getItemViewType(position) == FOOTER) columns else 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (viewType == FOOTER) footerLayout else itemLayout

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = if (position == itemCount -1) FOOTER else ITEM

    override fun getItemCount(): Int = list.size + 1

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (position == itemCount -1) listenerfooter?.invoke(viewHolder) else
            listenerItem?.invoke(viewHolder, list[position], position)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}