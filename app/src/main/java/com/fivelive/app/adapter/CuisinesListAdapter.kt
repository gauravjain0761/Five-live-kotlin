package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.Cuisine
import com.fivelive.app.Model.EditBusinessDetails
import com.fivelive.app.R

class CuisinesListAdapter(
    var context: Context,
    cuisineList: List<Cuisine?>?,
    details: EditBusinessDetails
) : RecyclerView.Adapter<CuisinesListAdapter.MyViewHolder>() {
    var cuisineList: List<Cuisine>
    var details: EditBusinessDetails

    init {
        this.cuisineList = details.cuisines
        this.details = details
        createList()
    }

    fun createList() {
        for (i in details.category.indices) {
            val catName = details.category[i]
            for (cuisine in details.cuisines) {
                if (catName.equals(cuisine.name, ignoreCase = true)) {
                    cuisine.isSelected = true
                    break
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.cusinies_list_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cuisineModel = cuisineList[position]
        holder.checkBox.text = cuisineModel.name
        if (cuisineModel.isSelected) {
            holder.checkBox.isChecked = true
        }
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cuisineModel.isSelected = true
            } else {
                cuisineModel.isSelected = false
            }
        }
    }

    override fun getItemCount(): Int {
        return cuisineList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkBox: CheckBox

        init {
            checkBox = itemView.findViewById<View>(R.id.checkBox) as CheckBox
        }
    }
}