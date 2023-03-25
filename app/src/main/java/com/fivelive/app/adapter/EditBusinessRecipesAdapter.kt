package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.R

class EditBusinessRecipesAdapter constructor(
    var context: Context,
    var recipesImagesList: List<String>
) : RecyclerView.Adapter<EditBusinessRecipesAdapter.MyViewHolder>() {
    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.recipes_images_single_row, parent, false)
        return MyViewHolder(view)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == 0) {
            holder.recipeImageView.setImageDrawable(
                context.getResources().getDrawable(R.drawable.img_a)
            )
        } else if (position == 1) {
            holder.recipeImageView.setImageDrawable(
                context.getResources().getDrawable(R.drawable.img_b)
            )
        } else if (position == 2) {
            holder.recipeImageView.setImageDrawable(
                context.getResources().getDrawable(R.drawable.img_c)
            )
        } else if (position == 3) {
            holder.recipeImageView.setImageDrawable(
                context.getResources().getDrawable(R.drawable.img_d)
            )
        }
    }

    public override fun getItemCount(): Int {
        return recipesImagesList.size
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recipeImageView: ImageView

        init {
            recipeImageView = itemView.findViewById<View>(R.id.recipeImageView) as ImageView
        }
    }
}