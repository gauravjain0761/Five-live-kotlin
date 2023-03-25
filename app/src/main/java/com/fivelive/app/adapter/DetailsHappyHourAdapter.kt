package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.HappyData
import com.fivelive.app.R

class DetailsHappyHourAdapter constructor(
    var context: Context,
    var happyHoursList: List<HappyData>
) : RecyclerView.Adapter<DetailsHappyHourAdapter.MyViewHolder>() {
    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.details_happy_hour_single_row, parent, false)
        return MyViewHolder(view)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val happyData: HappyData = happyHoursList.get(position)
        holder.day_name_tv.setText(happyData.days)
        if ((happyData.status == "Open")) {
            holder.timeTextView.setText(happyData.startTime + " - " + happyData.endTime)
        } else {
            holder.timeTextView.setText(happyData.status)
        }
    }

    public override fun getItemCount(): Int {
        return happyHoursList.size
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var day_name_tv: TextView
        var timeTextView: TextView

        init {
            day_name_tv = itemView.findViewById<View>(R.id.day_name_tv) as TextView
            timeTextView = itemView.findViewById<View>(R.id.timeTextView) as TextView
        }
    }
}