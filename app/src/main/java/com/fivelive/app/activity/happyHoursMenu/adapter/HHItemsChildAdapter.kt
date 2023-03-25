package com.fivelive.app.activity.happyHoursMenu.adapter

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
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil

class HHItemsChildAdapter(var context: Context?, var categoryDataList: List<CategoryData>) :
    RecyclerView.Adapter<HHItemsChildAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.menu_item_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = categoryDataList[position]
        holder.item_name_tv.text = data.name
        holder.item_size_tv.text = data.size + " Size"
        holder.item_price_tv.text = AppConstant.CURRENCY_SYMBOL + data.offerPrice
        AppUtil.loadLargeImage(context, data.image, holder.item_imv)
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
        var item_price_tv: TextView
        var item_imv: ImageView

        init {
            item_name_tv = itemView.findViewById<View>(R.id.item_name_tv) as TextView
            item_size_tv = itemView.findViewById<View>(R.id.item_size_tv) as TextView
            item_price_tv = itemView.findViewById<View>(R.id.item_price_tv) as TextView
            item_imv = itemView.findViewById<View>(R.id.item_imv) as ImageView
        }
    }
}