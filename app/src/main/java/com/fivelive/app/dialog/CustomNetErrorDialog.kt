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
import com.fivelive.app.R

class CustomNetErrorDialog(context: Context?, var onButtonClick: () -> Unit) : Dialog(
    context!!
) {
    var okayBtn: Button? = null

    init {
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_for_internet)
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window!!.setGravity(Gravity.CENTER_HORIZONTAL)
        okayBtn = findViewById<View>(R.id.okayBtn) as Button
        okayBtn!!.setOnClickListener {
            dismiss()
            onButtonClick()
        }
    }
}