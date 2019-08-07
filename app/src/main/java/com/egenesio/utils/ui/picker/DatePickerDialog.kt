package com.egenesio.utils.ui.picker

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.widget.DatePicker
import java.util.*

typealias OnDateSelectedListener = (date: Calendar) -> Unit

class DatePickerDialog: DialogFragment(), android.app.DatePickerDialog.OnDateSetListener {

    companion object {
        private const val TAG = "datePickerDialog"

        fun withDate(date: Date, fromToday: Boolean = false) = DatePickerDialog().apply {
            previousDate = Calendar.getInstance().apply {
                time = date
            }

            if (fromToday) minDate = Calendar.getInstance()
        }
    }

    private var minDate: Calendar? = null
    private var previousDate: Calendar? = null
    private var listener: OnDateSelectedListener? = null

    fun show(manager: FragmentManager, listener: OnDateSelectedListener) {
        super.show(manager, TAG)

        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = previousDate ?: Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        val a = android.app.DatePickerDialog(activity, this, year, month, day)

        a.datePicker.minDate = minDate?.timeInMillis ?: 0

        return a
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener?.let {
            val date = Calendar.getInstance()
            date.set(Calendar.YEAR, year)
            date.set(Calendar.MONTH, month)
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            it.invoke(date)
        }
    }

}