package com.fivelive.app.dialog

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment : DialogFragment(), OnDateSetListener {
    var listener: DateCallbackListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val cldr = Calendar.getInstance()
        cldr.add(Calendar.YEAR, -5)
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]
        // Create a new instance of TimePickerDialog and return it
        val picker = DatePickerDialog(requireActivity(), this, year, month, day)
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        var d: Date? = null
        try {
            d = sdf.parse("31/12/2015")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        picker.datePicker.maxDate = d!!.time
        return picker
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        listener!!.getSelectedDate(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
    }

    interface DateCallbackListener {
        fun getSelectedDate(item: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (context is DateCallbackListener) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement ItemClickListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        const val TAG = "DatePickerFragment"
    }
}