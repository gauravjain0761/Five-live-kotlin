package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.R

class CategoryAdapter(var context: Context, var dayList: List<String>) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {
    var categoryImages = intArrayOf(
        R.drawable.now,
        R.drawable.brunch,
        R.drawable.food,
        R.drawable.live_music,
        R.drawable.mexican
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.category_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name = dayList[position]
        if (position == 0) {
            holder.categoryNameTextView.text = name
            holder.categoryImageView.setImageResource(categoryImages[position])
        } else if (position == 1) {
            holder.categoryNameTextView.text = name
            holder.categoryImageView.setImageResource(categoryImages[position])
        } else if (position == 2) {
            holder.categoryNameTextView.text = name
            holder.categoryImageView.setImageResource(categoryImages[position])
        } else if (position == 3) {
            holder.categoryNameTextView.text = name
            holder.categoryImageView.setImageResource(categoryImages[position])
        } else if (position == 4) {
            holder.categoryNameTextView.text = name
            holder.categoryImageView.setImageResource(categoryImages[position])
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryNameTextView: TextView
        var categoryImageView: ImageView

        init {
            categoryNameTextView =
                itemView.findViewById<View>(R.id.categoryNameTextView) as TextView
            categoryImageView = itemView.findViewById<View>(R.id.categoryImageView) as ImageView
        }
    }
}