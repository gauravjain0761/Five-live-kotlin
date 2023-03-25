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
import android.widget.ImageView
import android.widget.TextView
import com.fivelive.app.R

class LogoutConfirmationDialog(context: Context?, var onButtonClicked: ()-> Unit) : Dialog(
    context!!
) {
    var message: String? = null
    var alert: String? = null
    var messageTextView: TextView? = null
    var alertTextView: TextView? = null
    var yesBtn: Button? = null
    var noBtn: Button? = null
    var closeImage_imv: ImageView? = null

    init {
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.logout_confirmation_dialog)
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window!!.setGravity(Gravity.CENTER_HORIZONTAL)
        yesBtn = findViewById<View>(R.id.yesBtn) as Button
        noBtn = findViewById<View>(R.id.noBtn) as Button
        noBtn!!.setOnClickListener { dismiss() }
        yesBtn!!.setOnClickListener {
            dismiss()
           onButtonClicked()
        }
    }
}