package com.fivelive.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.FilterList
import com.fivelive.app.Model.ModelTest
import com.fivelive.app.R

class QuickFilterChkboxAdapter constructor(
    var context: Context,
    var filterLists: List<FilterList>,
    var modelTest: ModelTest
) : RecyclerView.Adapter<QuickFilterChkboxAdapter.MyViewHolder>() {
    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.quick_filter_check_box_single_row, parent, false)
        return MyViewHolder(view)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: FilterList = filterLists.get(position)
        holder.qf_checkBox.setText(data.name)
        for (name: String in modelTest.hhFilterList) {
            if ((name == data.name)) {
                holder.qf_checkBox.setChecked(true)
                data.isSelected = true
                Log.d("onBindViewHolder", "onBindViewHolder: " + name)
                break
            }
        }
        holder.qf_checkBox.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            public override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                data.isSelected = isChecked
            }
        })
    }

    public override fun getItemCount(): Int {
        return filterLists.size
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var qf_checkBox: CheckBox

        init {
            qf_checkBox = itemView.findViewById(R.id.qf_checkBox)
        }
    }
}