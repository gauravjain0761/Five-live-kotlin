package com.fivelive.app.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.sql.Time
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class TimePickerFragment : DialogFragment(), OnTimeSetListener {
    var listener: TimeCallbackListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // listener.getSelectedTime(hourOfDay + ":" + minute+" "+AM_PM);
        listener!!.getSelectedTime(getTime(hourOfDay, minute))
    }

    private fun getTime(hr: Int, min: Int): String {
        val tme = Time(hr, min, 0) //seconds by default set to zero
        val formatter: Format
        formatter = SimpleDateFormat("hh:mm a")
        return formatter.format(tme)
    }

    interface TimeCallbackListener {
        fun getSelectedTime(item: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (mFragment != null) {
            if (mFragment is TimeCallbackListener) {
                mFragment as TimeCallbackListener?
            } else {
                throw RuntimeException(
                    context.toString()
                            + " must implement ItemClickListener"
                )
            }
        } else {
            if (context is TimeCallbackListener) {
                context
            } else {
                throw RuntimeException(
                    context.toString()
                            + " must implement ItemClickListener"
                )
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        const val TAG = "TimePickerFragment"
        var mFragment: Fragment? = null
        var mContext: Context? = null
        fun newInstance(context: Context?, fragment: Fragment?): TimePickerFragment {
            mFragment = fragment
            mContext = context
            return TimePickerFragment()
        }
    }
}