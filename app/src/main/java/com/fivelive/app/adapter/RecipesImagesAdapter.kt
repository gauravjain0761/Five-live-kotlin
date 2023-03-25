package com.fivelive.app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.fivelive.app.R
import com.fivelive.app.activity.ShowImagesActivity
import com.fivelive.app.util.AppConstant
import java.io.Serializable

class RecipesImagesAdapter(var context: Context, var imagesList: List<String>) :
    RecyclerView.Adapter<RecipesImagesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.recipes_images_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imgPath = imagesList[position]
        /* Glide.with(context)
                .load(imgPath)
                .placeholder(R.drawable.rest_slider)
                .transform(new RoundedCorners(6))
                .into(holder.recipeImageView);*/
        val myOptions = RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL) // .override(75)
            .centerCrop()
        Glide.with(context)
            .load(imgPath) //   .apply(myOptions)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(true)
            .placeholder(R.drawable.rest_slider) // .transform(new RoundedCorners(6))
            .into(holder.recipeImageView)


        // AppUtil.loadHorizontalSmallImage(context,imgPath,holder.recipeImageView);
        holder.recipeImageView.setOnClickListener { showImagePreview(position) }
    }

    fun showImagePreview(position: Int) {
        //  Intent intent = new Intent(DetailsActivity.this, PreviewImageActivity.class);
        val intent = Intent(context, ShowImagesActivity::class.java)
        intent.putExtra(AppConstant.IMAGE_INDEX, position)
        intent.putExtra(AppConstant.IMAGES_LIST, imagesList as Serializable)
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return imagesList.size
        //  return 4;
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recipeImageView: ImageView

        init {
            recipeImageView = itemView.findViewById(R.id.recipeImageView)
        }
    }
}