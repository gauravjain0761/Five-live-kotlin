package com.fivelive.app.activity.amenities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fivelive.app.Model.Amenity
import com.fivelive.app.R

class BizAmenitiesAdapter(var context: Context?, var amenitiesList: List<Amenity>) :
    RecyclerView.Adapter<BizAmenitiesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.restaurant_amenities_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val amenityModel = amenitiesList[position]
        // holder.amenitiesImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.outdoor));
        holder.amenitiesTextView.text = amenityModel.name
        Glide.with(context!!)
            .load(amenityModel.image)
            .placeholder(R.drawable.outdoor)
            .into(holder.amenitiesImageView)
    }

    override fun getItemCount(): Int {
        return amenitiesList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var amenitiesImageView: ImageView
        var amenitiesTextView: TextView

        init {
            amenitiesImageView = itemView.findViewById<View>(R.id.amenitiesImageView) as ImageView
            amenitiesTextView = itemView.findViewById<View>(R.id.amenitiesTextView) as TextView
        }
    }
}