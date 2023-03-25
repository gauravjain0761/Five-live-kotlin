package com.fivelive.app.activity.brunchMenu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.HHDetails
import com.fivelive.app.R

class BusinessBrunchDetailsAdapter(
    var context: Context?,
    var hhDetailsArrayList: List<HHDetails>?
) : RecyclerView.Adapter<BusinessBrunchDetailsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.business_brunch_details_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val hhDetails = hhDetailsArrayList!![position]
        holder.dayName_tv.text = hhDetails.days + ":"
        holder.start_end_time_tv.text = hhDetails.startTime + " - " + hhDetails.endTime
        holder.drinks_tv.text = hhDetails.drink
        holder.food_tv.text = hhDetails.food
        holder.linearLayout.setOnClickListener {
            if (holder.constraintLayout.visibility == View.VISIBLE) {
                holder.constraintLayout.visibility = View.GONE
            } else {
                holder.constraintLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return hhDetailsArrayList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagesRecyclerView: RecyclerView? = null
        var constraintLayout: ConstraintLayout
        var linearLayout: LinearLayout
        var dayName_tv: TextView
        var start_end_time_tv: TextView
        var drinks_tv: TextView
        var food_tv: TextView

        init {
            constraintLayout = itemView.findViewById(R.id.constraintLayout)
            linearLayout = itemView.findViewById(R.id.linearLayout)
            dayName_tv = itemView.findViewById(R.id.dayName_tv)
            start_end_time_tv = itemView.findViewById(R.id.start_end_time_tv)
            drinks_tv = itemView.findViewById(R.id.drinks_tv)
            food_tv = itemView.findViewById(R.id.food_tv)
        }
    }
}