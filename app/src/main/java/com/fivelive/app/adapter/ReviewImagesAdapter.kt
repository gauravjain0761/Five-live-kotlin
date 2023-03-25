package com.fivelive.app.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.R

class ReviewImagesAdapter(var context: Context, var imagesURIList: MutableList<String>) :
    RecyclerView.Adapter<ReviewImagesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.my_sanju, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(imagesURIList[position], bmOptions)
        holder.imageView.setImageBitmap(bitmap)
        holder.cross_imv.setOnClickListener {
            imagesURIList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return imagesURIList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var cross_imv: ImageView

        init {
            imageView = itemView.findViewById<View>(R.id.image) as ImageView
            cross_imv = itemView.findViewById<View>(R.id.cross_imv) as ImageView
        }
    }
}