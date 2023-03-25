package com.fivelive.app.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.fivelive.app.R

class FaqsExpandableListAdapter constructor(
    private val _context: Context, // header titles
    private val _listDataHeader: List<String>, // child data in format of header title, child title
    private val _listDataChild: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {
    public override fun getChild(groupPosition: Int, childPosititon: Int): Any {
        return _listDataChild.get(_listDataHeader.get(groupPosition))!!.get(childPosititon)
    }

    public override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    public override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View,
        parent: ViewGroup
    ): View {
        var convertView: View = convertView
        val childText: String = getChild(groupPosition, childPosition) as String
        if (convertView == null) {
            val infalInflater: LayoutInflater =
                _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.adapter_list_item, null)
        }
        val txtListChild: TextView = convertView.findViewById<View>(R.id.text_desc) as TextView
        txtListChild.setText(childText)
        return convertView
    }

    public override fun getChildrenCount(groupPosition: Int): Int {
        return _listDataChild.get(_listDataHeader.get(groupPosition))!!.size
    }

    public override fun getGroup(groupPosition: Int): Any {
        return _listDataHeader.get(groupPosition)
    }

    public override fun getGroupCount(): Int {
        return _listDataHeader.size
    }

    public override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    public override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View,
        parent: ViewGroup
    ): View {
        var convertView: View = convertView
        val headerTitle: String = getGroup(groupPosition) as String
        if (convertView == null) {
            val infalInflater: LayoutInflater =
                _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.adapter_list_group, null)
        }
        val lblListHeader: TextView = convertView.findViewById<View>(R.id.text_title) as TextView
        lblListHeader.setTypeface(null, Typeface.BOLD)
        lblListHeader.setText(headerTitle)
        return convertView
    }

    public override fun hasStableIds(): Boolean {
        return false
    }

    public override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}