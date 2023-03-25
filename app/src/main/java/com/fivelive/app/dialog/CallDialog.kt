package com.fivelive.app.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.fivelive.app.R

class CallDialog(context: Context?, var phoneNumber: String, var onButtonClicked: () -> Unit) :
    Dialog(
        context!!
    ) {
    var okayBtn: Button? = null
    var phoneNumber_tv: TextView? = null
    var cancel_tv: TextView? = null
    var call_tv: TextView? = null

    init {
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_call_dialog)
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window!!.setGravity(Gravity.CENTER_HORIZONTAL)
        phoneNumber_tv = findViewById<View>(R.id.phoneNumber_tv) as TextView
        phoneNumber_tv!!.text = phoneNumber
        cancel_tv = findViewById<View>(R.id.cancel_tv) as TextView
        call_tv = findViewById<View>(R.id.call_tv) as TextView
        call_tv!!.setOnClickListener {
            dismiss()
            onButtonClicked()
        }
        cancel_tv!!.setOnClickListener { dismiss() }
    }
}