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

class ResetConfirmedDialog(context: Context?, var onButtonClicked: () -> Unit) : Dialog(
    context!!
) {
    //    var message: String
//    var alert: String
    var messageTextView: TextView? = null
    var alertTextView: TextView? = null
    var okay_btn: Button? = null

    init {
//        message = message
//        alert = alert
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.reset_confirmed_dialog)
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window!!.setGravity(Gravity.CENTER_HORIZONTAL)
        okay_btn = findViewById<View>(R.id.okay_btn) as Button
        alertTextView = findViewById<View>(R.id.alertTextView) as TextView
        messageTextView = findViewById<View>(R.id.messageTextView) as TextView

        /*alertTextView.setText(alert);
        messageTextView.setText(message);*/okay_btn!!.setOnClickListener {
            dismiss()
            onButtonClicked()
        }
    }
}