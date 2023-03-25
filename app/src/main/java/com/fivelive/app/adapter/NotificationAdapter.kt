package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.Notification
import com.fivelive.app.R

class NotificationAdapter(var context: Context, var notificationList: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.notification_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = notificationList[position]
        holder.notification_tv.text = model.title
        holder.time_tv.text = model.createdAt
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var notification_tv: TextView
        var time_tv: TextView

        init {
            notification_tv = itemView.findViewById<View>(R.id.notification_tv) as TextView
            time_tv = itemView.findViewById<View>(R.id.time_tv) as TextView
        }
    }
}