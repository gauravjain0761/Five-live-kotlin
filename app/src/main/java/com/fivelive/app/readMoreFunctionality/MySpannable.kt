package com.fivelive.app.readMoreFunctionality

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

open class MySpannable(isUnderline: Boolean) : ClickableSpan() {
    private var isUnderline = true

    /**
     * Constructor
     */
    init {
        this.isUnderline = isUnderline
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderline
        ds.color = Color.parseColor("#0042B9")
    }

    override fun onClick(widget: View) {}
}