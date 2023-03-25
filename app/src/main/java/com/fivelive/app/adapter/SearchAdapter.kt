package com.fivelive.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.SuggestionModel
import com.fivelive.app.R
import com.fivelive.app.activity.SearchActivity

class SearchAdapter(var context: Context, var suggestionList: List<SuggestionModel>) :
    RecyclerView.Adapter<SearchAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.search_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = suggestionList[position]
        holder.dayNameTextView.text = model.name
        if (model.isFromServer) {
            holder.icon_imv.setImageDrawable(context.getDrawable(R.drawable.ic_restaurant))
        } else {
            holder.icon_imv.setImageDrawable(context.getDrawable(R.drawable.ic_location_ph))
        }
        holder.itemView.setOnClickListener {
            if (model.isFromServer) {
                (context as SearchActivity).dispatchToDetailsScreen(model.businessId)
            } else {
                //here business id equal to places id.
                (context as SearchActivity).fetchPlacesRequest(model.businessId, model.name)
            }
        }
    }

    override fun getItemCount(): Int {
        return suggestionList.size
        //return 10;
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayNameTextView: TextView
        var icon_imv: ImageView

        init {
            dayNameTextView = itemView.findViewById<View>(R.id.dayNameTextView) as TextView
            icon_imv = itemView.findViewById<View>(R.id.icon_imv) as ImageView
        }
    }
}