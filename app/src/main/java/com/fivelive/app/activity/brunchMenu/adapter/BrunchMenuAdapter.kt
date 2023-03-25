package com.fivelive.app.activity.brunchMenu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.HHMenu
import com.fivelive.app.R
import com.fivelive.app.activity.brunchMenu.fragment.RestaurantBrunchMenuFragment
import com.fivelive.app.util.AppUtil

class BrunchMenuAdapter(
    var context: Context?,
    var brunchMenuArrayList: List<HHMenu>?,
    var fragment: Fragment
) : RecyclerView.Adapter<BrunchMenuAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.business_happy_hour_menu_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val menu = brunchMenuArrayList!![position]
        AppUtil.loadMenuImage(context, menu.image, holder.itemImageView)
        holder.itemNameTextView.text = menu.title
        holder.itemImageView.setOnClickListener {
            (fragment as RestaurantBrunchMenuFragment).showImagePreview(
                position
            )
        }
    }

    override fun getItemCount(): Int {
        return brunchMenuArrayList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImageView: ImageView
        var itemNameTextView: TextView

        init {
            itemImageView = itemView.findViewById<View>(R.id.itemImageView) as ImageView
            itemNameTextView = itemView.findViewById<View>(R.id.itemNameTextView) as TextView
        }
    }
}