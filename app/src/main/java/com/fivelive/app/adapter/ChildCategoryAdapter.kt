package com.fivelive.app.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.CategoryData
import com.fivelive.app.R
import com.fivelive.app.activity.SetHappyHourActivity
import com.fivelive.app.activity.SetReverseHappyHourActivity
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil

class ChildCategoryAdapter(var context: Context, var categoryDataList: List<CategoryData>) :
    RecyclerView.Adapter<ChildCategoryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.edit_hh_item_child_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = categoryDataList[position]
        holder.item_name_tv.text = data.name
        holder.item_size_tv.text = data.size + " Size"
        holder.item_offer_price_tv.text = AppConstant.CURRENCY_SYMBOL + data.offerPrice
        holder.item_regular_price_tv.text = AppConstant.CURRENCY_SYMBOL + data.regularPrice
        holder.percentage_off_tv.text = data.percentage + "% Off"
        AppUtil.loadLargeImage(context, data.image, holder.item_imv)
        holder.delete_imv.setOnClickListener {
            if (context is SetHappyHourActivity) {
                (context as SetHappyHourActivity).showDeleteConfirmationDialog(data.itemId)
            } else if (context is SetReverseHappyHourActivity) {
                (context as SetReverseHappyHourActivity).showDeleteConfirmationDialog(data.itemId)
            }
        }
        holder.edit_imv.setOnClickListener {
            if (context is SetHappyHourActivity) {
                (context as SetHappyHourActivity).dispatchToHHAddItemActivity(data.itemId)
            } else if (context is SetReverseHappyHourActivity) {
                (context as SetReverseHappyHourActivity).dispatchToHHAddItemActivity(data.itemId)
            }
        }
        holder.item_imv.setOnClickListener {
            AppUtil.showLargeImage(
                context as Activity,
                holder.item_imv,
                data.image
            )
        }
    }

    override fun getItemCount(): Int {
        return categoryDataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item_name_tv: TextView
        var item_size_tv: TextView
        var item_offer_price_tv: TextView
        var item_regular_price_tv: TextView
        var percentage_off_tv: TextView
        var item_imv: ImageView
        var edit_imv: ImageView
        var delete_imv: ImageView

        init {
            item_name_tv = itemView.findViewById<View>(R.id.item_name_tv) as TextView
            item_size_tv = itemView.findViewById<View>(R.id.item_size_tv) as TextView
            item_offer_price_tv = itemView.findViewById<View>(R.id.item_offer_price_tv) as TextView
            item_regular_price_tv =
                itemView.findViewById<View>(R.id.item_regular_price_tv) as TextView
            percentage_off_tv = itemView.findViewById<View>(R.id.percentage_off_tv) as TextView
            item_imv = itemView.findViewById<View>(R.id.item_imv) as ImageView
            edit_imv = itemView.findViewById<View>(R.id.edit_imv) as ImageView
            delete_imv = itemView.findViewById<View>(R.id.delete_imv) as ImageView
        }
    }
}