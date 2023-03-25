package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.DetailCategory
import com.fivelive.app.R
import com.fivelive.app.activity.DetailsActivity

class DetailsCategoryAdapter(var context: Context, var detailCategoryList: List<DetailCategory>) :
    RecyclerView.Adapter<DetailsCategoryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.details_category_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val category = detailCategoryList[position]
        holder.categoryImageView.setImageResource(category.image)
        holder.categoryTextView.text = category.name
        holder.itemView.setOnClickListener(View.OnClickListener {
            (context as DetailsActivity).categoryListListener(category.name.trim { it <= ' ' }
                .replace("\n", " "))
        })
        /*if(category.isStatus() == 1){
            holder.parentLayout.setVisibility(View.VISIBLE);
        }else{
            holder.parentLayout.setVisibility(View.GONE);
        }*/
        /*if(position == 0){
            holder.categoryImageView.setImageResource(R.drawable.hh_menu);
            holder.categoryTextView.setText("HH Menu");
        }else if(position == 1){
            holder.categoryImageView.setImageResource(R.drawable.reverse_hh_menu);
           // holder.categoryTextView.setText(R.string.reverse_hh_menu);
            holder.categoryTextView.setText("Rev HH Menu");
        }else if(position == 2){
            holder.categoryImageView.setImageResource(R.drawable.brunch_b);
            holder.categoryTextView.setText("Brunch");
        }else if(position == 3){
            holder.categoryImageView.setImageResource(R.drawable.call);
            holder.categoryTextView.setText("Call");
        }else if(position == 4){
            holder.categoryImageView.setImageResource(R.drawable.web);
            holder.categoryTextView.setText("Website");
        }else if(position == 5){
            holder.categoryImageView.setImageResource(R.drawable.main_menu);
            holder.categoryTextView.setText("Menu");
        }else if(position == 6){
            holder.categoryImageView.setImageResource(R.drawable.info);
            holder.categoryTextView.setText("Info");
        }*/
    }

    override fun getItemCount(): Int {
        return detailCategoryList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryImageView: ImageView
        var categoryTextView: TextView
        var parentLayout: ConstraintLayout

        init {
            categoryTextView = itemView.findViewById<View>(R.id.categoryTextView) as TextView
            categoryImageView = itemView.findViewById<View>(R.id.categoryImageView) as ImageView
            parentLayout = itemView.findViewById<View>(R.id.parentLayout) as ConstraintLayout
        }
    }
}