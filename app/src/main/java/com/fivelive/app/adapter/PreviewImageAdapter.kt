package com.fivelive.app.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fivelive.app.R
import com.fivelive.app.customView.TouchImageView

class PreviewImageAdapter(private val context: Context, private val imageList: List<String>) :
    RecyclerView.Adapter<PreviewImageAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.preview_image_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imagePath = imageList[position]
        Glide.with(context)
            .load(imagePath)
            .placeholder(R.drawable.main_img)
            .into(holder.previewImageView)
        // holder.tvName.setText(imageList.get(position));
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var previewImageView: TouchImageView

        init {
            previewImageView = itemView.findViewById(R.id.previewImageView)
        }
    }
}