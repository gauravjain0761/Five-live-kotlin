package com.fivelive.app.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

class TextWatcher(var inputLayout: TextInputLayout?, private var editText: EditText?) : TextWatcher {
    init {
        addListener()
    }

    private fun addListener() {
        editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                inputLayout?.error = null
                inputLayout?.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {}
}