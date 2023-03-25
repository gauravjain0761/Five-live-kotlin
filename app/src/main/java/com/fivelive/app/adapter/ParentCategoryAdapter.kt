package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.Category
import com.fivelive.app.Model.CategoryData
import com.fivelive.app.R

class ParentCategoryAdapter(var context: Context, var categoryList: List<Category>) :
    RecyclerView.Adapter<ParentCategoryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.expendale_list_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val catModel = categoryList[position]
        holder.parentCategoryTextView.text = catModel.categoryName
        holder.categoryItemCount.visibility = View.GONE
        setDataOnList(holder, catModel.categoryData)
        holder.parentConstraintLayout.setOnClickListener {
            if (holder.childRecyclerView.visibility == View.VISIBLE) {
                holder.childRecyclerView.visibility = View.GONE
                holder.expendableImageView.setImageDrawable(context.resources.getDrawable(R.drawable.ddown))
            } else {
                holder.childRecyclerView.visibility = View.VISIBLE
                holder.expendableImageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_uparrow))
            }
        }
    }

    fun setDataOnList(holder: MyViewHolder, categoryDataList: List<CategoryData>) {
        val adapter = ChildCategoryAdapter(context, categoryDataList)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        holder.childRecyclerView.layoutManager = llm
        holder.childRecyclerView.adapter = adapter
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parentCategoryTextView: TextView
        var categoryItemCount: TextView
        var expendableImageView: ImageView
        var childRecyclerView: RecyclerView
        var parentConstraintLayout: ConstraintLayout

        init {
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView)
            parentConstraintLayout = itemView.findViewById(R.id.parentConstraintLayout)
            parentCategoryTextView = itemView.findViewById(R.id.parentCategoryTextView)
            categoryItemCount = itemView.findViewById(R.id.categoryItemCount)
            expendableImageView = itemView.findViewById(R.id.expendableImageView)
        }
    }
}