package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.MusicData
import com.fivelive.app.R

class DetailsLiveMusicAdapter constructor(
    var context: Context,
    var liveMusicList: List<MusicData>
) : RecyclerView.Adapter<DetailsLiveMusicAdapter.MyViewHolder>() {
    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.details_happy_hour_single_row, parent, false)
        return MyViewHolder(view)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        /*String dayName = dayList.get(position);
        if(position == 1){
            holder.timeTextView.setText("10:00 AM - 10:00 PM");
        }else if(position == 1){
            holder.timeTextView.setText("10:00 AM - 10:00 PM");
        }*/
        val data: MusicData = liveMusicList.get(position)
        holder.day_name_tv.setText(data.days)
        if ((data.status == "Open")) {
            holder.timeTextView.setText(data.startTime + " - " + data.endTime)
        } else {
            holder.timeTextView.setText(data.status)
        }
    }

    public override fun getItemCount(): Int {
        return liveMusicList.size
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