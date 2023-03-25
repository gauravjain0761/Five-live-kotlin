package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fivelive.app.Model.ReviewArray
import com.fivelive.app.R
import com.fivelive.app.util.AppUtil
import de.hdodenhof.circleimageview.CircleImageView

class ReviewAdapter(var context: Context, var reviewList: List<ReviewArray>) :
    RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.review_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = reviewList[position]
        holder.user_name_tv.text = model.userName
        holder.review_tv.text = model.review
        holder.rating_bar.rating = model.rating
        holder.date_tv.text = model.time
        Glide.with(context)
            .load(model.userImage)
            .placeholder(R.drawable.profile_other)
            .into(holder.user_imv)
        for (i in model.images.indices) {
            when (i) {
                0 -> {
                    holder.imv_one.visibility = View.VISIBLE
                    AppUtil.loadHorizontalSmallImage(context, model.images[i], holder.imv_one)
                }
                1 -> {
                    holder.imv_two.visibility = View.VISIBLE
                    AppUtil.loadHorizontalSmallImage(context, model.images[i], holder.imv_two)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var user_name_tv: TextView
        var review_tv: TextView
        var date_tv: TextView
        var user_imv: CircleImageView
        var rating_bar: RatingBar
        var imv_one: ImageView
        var imv_two: ImageView

        init {
            user_name_tv = itemView.findViewById<View>(R.id.user_name_tv) as TextView
            date_tv = itemView.findViewById<View>(R.id.date_tv) as TextView
            review_tv = itemView.findViewById<View>(R.id.review_tv) as TextView
            user_imv = itemView.findViewById<View>(R.id.user_imv) as CircleImageView
            rating_bar = itemView.findViewById<View>(R.id.rating_bar) as RatingBar
            imv_one = itemView.findViewById<View>(R.id.imv_one) as ImageView
            imv_two = itemView.findViewById<View>(R.id.imv_two) as ImageView
        }
    }
}