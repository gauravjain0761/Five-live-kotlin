package com.fivelive.app.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import com.fivelive.app.R

class UtilSpinnerAdapter(context: Activity, resouceId: Int, textviewId: Int, list: List<String>) :
    ArrayAdapter<Any?>(context, resouceId, textviewId, list), SpinnerAdapter {
    var context: Activity
    var list: List<String>
    var inflate: LayoutInflater

    init {
        inflate = context.layoutInflater
        this.context = context
        this.list = list
    }

    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        val view = super.getDropDownView(position, convertView, parent)
        if (convertView == null) {
            convertView = inflate.inflate(R.layout.spinner_drop_down_single_row, parent, false)
        }
        view.setBackgroundColor(context.resources.getColor(R.color.white))
        val tv = convertView.findViewById<View>(R.id.name) as TextView
        tv.textSize = 14.0f
        /*if (position == 0) {
            tv.setTextColor(context.getResources().getColor(R.color.gray_color));
        } else {
            tv.setTextColor(context.getResources().getColor(android.R.color.black));
        }*/tv.setTextColor(context.resources.getColor(android.R.color.black))
        tv.text = list[position]
        return convertView
    }
}