package io.upify.utils.ui.picker

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.widget.DatePicker
import android.widget.TimePicker
import java.util.*

typealias OnTimeSelectedListener = () -> Unit

class TimePickerDialog: androidx.fragment.app.DialogFragment(), android.app.TimePickerDialog.OnTimeSetListener {

    companion object {
        private const val TAG = "timePickerDialog"
    }

    private var listener: OnTimeSelectedListener? = null

    fun show(manager: FragmentManager, listener: OnTimeSelectedListener) {
        super.show(manager, TAG)

        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return android.app.TimePickerDialog(activity, this, 1, 1, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener?.let {
            it.invoke()
        }
    }

}