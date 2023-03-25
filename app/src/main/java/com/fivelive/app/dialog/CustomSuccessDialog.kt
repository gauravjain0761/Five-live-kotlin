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

class CustomSuccessDialog(
    context: Context?,
    var alert: String,
    var message: String?,
    var onButtonClicked: ()-> Unit
) : Dialog(
    context!!
) {
    var messageText: TextView? = null
    var alertText: TextView? = null
    var okayBtn: Button? = null

    init {
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_success_dialog)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setGravity(Gravity.CENTER_HORIZONTAL)
        okayBtn = findViewById<View>(R.id.okayBtn) as Button
        messageText = findViewById<View>(R.id.messageText) as TextView
        alertText = findViewById<View>(R.id.alertText) as TextView
        alertText!!.text = alert
        messageText!!.text = message
        okayBtn!!.setOnClickListener {
            dismiss()
            onButtonClicked()
        }
    }
}