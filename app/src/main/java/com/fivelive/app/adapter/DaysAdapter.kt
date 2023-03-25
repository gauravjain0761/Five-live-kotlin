package com.fivelive.app.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.DayModal
import com.fivelive.app.R
import com.fivelive.app.fragment.NewHomeFragment

class DaysAdapter(var context: Context, var dayList: List<DayModal>, var fragment: Fragment) :
    RecyclerView.Adapter<DaysAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.days_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dayModal = dayList[position]
        holder.dayNameTextView.text = dayModal.dayName
        if (dayModal.isSelected) {
            holder.dayNameTextView.background = context.getDrawable(R.drawable.blue_btn_bg)
            holder.dayNameTextView.setTextColor(context.resources.getColor(R.color.white))
        } else {
            holder.dayNameTextView.background = context.getDrawable(R.drawable.days_bg)
            holder.dayNameTextView.setTextColor(Color.parseColor("#4b5865"))
        }
        holder.dayNameTextView.setOnClickListener {
            if (dayModal.isSelected) {
                dayModal.isSelected = false
            } else {
                checkAlreadySelectedDay()
                dayModal.isSelected = true
            }
            (fragment as NewHomeFragment).filterListDaysVise(position)
            //notifyDataSetChanged();
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    fun checkAlreadySelectedDay() {
        for (dayModal in dayList) {
            if (dayModal.isSelected) {
                dayModal.isSelected = false
            }
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayNameTextView: TextView

        init {
            dayNameTextView = itemView.findViewById<View>(R.id.dayNameTextView) as TextView
        }
    }
}