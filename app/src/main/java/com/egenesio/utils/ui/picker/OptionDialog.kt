package com.egenesio.utils.ui.picker

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import com.egenesio.utils.R
import com.egenesio.utils.ui.extensions.inputText
import org.jetbrains.anko.*

typealias OptionDialogListener = ((selected: Int?) -> Unit)
typealias OptionDialogTitleListener = ((input: String?) -> Unit)
typealias OptionAdapterListener = ((selected: Int?) -> Unit)

enum class OptionDialogType(var prevInput: String? = null) {
    DEFAULT, TITLE_INPUT, SHOW_NONE
}

class OptionDialog(
    private val context: Context,
    private val title: String,
    private val items: List<String>,
    private val itemSelected: Int?,
    private val closeOnSelection: Boolean = false,
    private val itemLayout: Int = R.layout.item_list_simple,
    private val titleResId: Int = R.id.rdoTitle) {

    companion object {}

    var neutralButtonText: String = ""
    var positiveButonText: String = ""
    var negativeButtonText: String = ""

    var listener: OptionDialogListener? = null
    var titleListener: OptionDialogTitleListener? = null
    var selected: Int? = null

    val adapter = OptionAdapter(context, itemLayout, titleResId, items, itemSelected)

    private lateinit var dialog: DialogInterface
    private var titleInputEditText: EditText? = null

    init {
        adapter.listener = { selected ->
            this.selected = selected

            if (closeOnSelection) {
                listener?.invoke(selected)
                dialog.dismiss()
            }
            selected?.let {
                titleInputEditText?.inputText = items[it]
                titleInputEditText?.setSelection(titleInputEditText?.inputText?.length ?: 0)
            }
        }
    }

    fun show(type: OptionDialogType = OptionDialogType.DEFAULT) {
        dialog = context.alert {
            title = this@OptionDialog.title

            customView {
                linearLayout {
                    orientation = LinearLayout.VERTICAL

                    if (type == OptionDialogType.TITLE_INPUT) {
                        titleInputEditText = editText {
                            //hint = context.getString(R.string.checklist_form_field_name) TODO
                            padding = dip(16)
                            type.prevInput?.let {
                                inputText = it
                                setSelection(inputText.length)
                            }
                        }
                    }

                    listView {
                        adapter = this@OptionDialog.adapter
                    }.lparams(width = matchParent, height = matchParent)

                    padding = dip(16)
                }
            }

            if (closeOnSelection) return@alert

            if (type == OptionDialogType.SHOW_NONE) {
                neutralPressed(neutralButtonText) {listener?.invoke(-1)}
            }

            negativeButton(negativeButtonText) {listener?.invoke(null)}
            positiveButton(positiveButonText) {
                if (type == OptionDialogType.TITLE_INPUT) {
                    titleListener?.invoke(titleInputEditText?.inputText)
                } else {
                    val sel = if (selected == itemSelected) null else selected
                    listener?.invoke(sel)
                }
            }

        }.show()
    }
}

class OptionAdapter(
    context: Context,
    private val itemLayout: Int,
    private val titleResId: Int,
    private val items: List<String>, itemSelected: Int?): ArrayAdapter<String>(context, itemLayout, titleResId, items) {

    var listener: OptionAdapterListener? = null

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var selected: Int? = itemSelected
        private set

    var dialogType: OptionDialogType = OptionDialogType.DEFAULT

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder

        val view: View

        if (convertView == null) {
            holder = ViewHolder()
            view = inflater.inflate(itemLayout, null)
            holder.rdo = view.findViewById(titleResId)

            view.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        holder.rdo?.text = items[position]
        holder.rdo?.isChecked = position == selected

        holder.rdo?.setOnClickListener {
            selected = position
            listener?.invoke(selected)
            notifyDataSetChanged()
        }

        return view
    }

    private class ViewHolder {
        var rdo: RadioButton? = null
    }
}