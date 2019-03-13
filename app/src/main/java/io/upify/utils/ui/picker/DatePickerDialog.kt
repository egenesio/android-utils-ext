package io.upify.utils.ui.picker

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.widget.DatePicker
import java.util.*

typealias OnDateSelectedListener = (date: Calendar) -> Unit

class DatePickerDialog: DialogFragment(), android.app.DatePickerDialog.OnDateSetListener {

    companion object {
        private const val TAG = "datePickerDialog"

        fun withDate(date: Date) = DatePickerDialog().apply {
            previousDate =Calendar.getInstance().apply {
                time = date
            }
        }
    }

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
        return android.app.DatePickerDialog(activity, this, year, month, day)
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