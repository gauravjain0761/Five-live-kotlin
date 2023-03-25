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
import com.fivelive.app.util.AppConstant

class CustomSkipUserDialog(context: Context, var onSuccess: (String) -> Unit) : Dialog(context) {
    //    var message: String
//    var alert: String
    var messageTextView: TextView? = null
    var alertTextView: TextView? = null
    var limited_btn: Button? = null
    var register_btn: Button? = null
    var closeImage_imv: ImageView? = null

    init {
//        message = message
//        alert = alert
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_skip_user_dialog)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setGravity(Gravity.CENTER_HORIZONTAL)
        register_btn = findViewById<View>(R.id.register_btn) as Button
        limited_btn = findViewById<View>(R.id.limited_btn) as Button
        register_btn!!.setOnClickListener {
            dismiss()
            onSuccess(AppConstant.REGISTER)
        }
        limited_btn!!.setOnClickListener {
            dismiss()
            onSuccess(AppConstant.LIMITED)
        }
    }
}