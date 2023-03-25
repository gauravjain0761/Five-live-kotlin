package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.*
import com.fivelive.app.R
import com.fivelive.app.activity.EditBusinessActivity
import com.fivelive.app.util.AppUtil

class EditBusinessRecipesImagesAdapter constructor(
    var context: Context,
    var recipesImagesList: MutableList<Image>
) : RecyclerView.Adapter<EditBusinessRecipesImagesAdapter.MyViewHolder>() {
    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View =
            inflater.inflate(R.layout.edit_business_recipe_image_single_row, parent, false)
        return MyViewHolder(view)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val image: Image = recipesImagesList.get(position)
        AppUtil.loadHorizontalSmallImage(context, image.image, holder.recipeImageView)
        holder.delete_imv.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                (context as EditBusinessActivity).showDeleteConfirmationDialog(
                    image.id,
                    position
                )
            }
        })
    }

    public override fun getItemCount(): Int {
        return recipesImagesList.size
    }

    fun removeMenuItemFromList(itemId: String?, position: Int) {
        recipesImagesList.removeAt(position)
        notifyDataSetChanged()
        /*for (Image menu : recipesImagesList) {
            if (menu.getId() != null)
                if (menu.getId().equals(itemId)) {
                    recipesImagesList.remove(menu);
                    notifyDataSetChanged();
                }
        }*/
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recipeImageView: ImageView
        var delete_imv: ImageView

        init {
            recipeImageView = itemView.findViewById<View>(R.id.recipeImageView) as ImageView
            delete_imv = itemView.findViewById<View>(R.id.delete_imv) as ImageView
        }
    }
}