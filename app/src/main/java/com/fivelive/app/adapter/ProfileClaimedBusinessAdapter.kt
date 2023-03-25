package com.fivelive.app.adapter

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.MyClaimBusiness
import com.fivelive.app.R
import com.fivelive.app.activity.DummyMapActivity
import com.fivelive.app.activity.ProfileActivity
import com.fivelive.app.readMoreFunctionality.ExpandableTextView
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil

class ProfileClaimedBusinessAdapter constructor(
    var context: Context,
    var myClaimBusinessList: List<MyClaimBusiness>
) : RecyclerView.Adapter<ProfileClaimedBusinessAdapter.MyViewHolder>() {
    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        //  View view = inflater.inflate(R.layout.restaurant_details_lauout, parent, false);
        val view: View = inflater.inflate(R.layout.restaurant_details_lauout_new, parent, false)
        return MyViewHolder(view)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: MyClaimBusiness = myClaimBusinessList.get(position)
        holder.setRecipesImagesList(model.images)
        holder.businessName_tv.setText(model.businessName)
        AppUtil.setHappyHoursTime(
            holder.hh_today_tv,
            model.hhHoursStartTime,
            model.hhHoursEndTime
        )
        AppUtil.setMusicLiveTime(
            holder.lm_today_tv,
            model.musicStartTime,
            model.musicEndTime
        )
        if (model.hh_drink != null && !(model.hh_drink == "")) {
            holder.drinks_tv.setText(Html.fromHtml(model.hh_drink))
            if (model.hh_drink.length > AppConstant.EXPENDABLE_TEXT_LENGTH) {
                ExpandableTextView.makeTextViewResizable(
                    holder.drinks_tv,
                    2,
                    AppConstant.SEE_MORE,
                    true
                )
            }
        } else {
            holder.drinks_tv.setVisibility(View.GONE)
            holder.drink_tv.setVisibility(View.GONE)
        }
        if (model.hh_food != null && !(model.hh_food == "")) {
            holder.food_tv.setText(Html.fromHtml(model.hh_food))
            if (model.hh_food.length > AppConstant.EXPENDABLE_TEXT_LENGTH) {
                ExpandableTextView.makeTextViewResizable(
                    holder.food_tv,
                    2,
                    AppConstant.SEE_MORE,
                    true
                )
            }
        } else {
            holder.food_tv.setVisibility(View.GONE)
            holder.food_tvs.setVisibility(View.GONE)
        }
        holder.google_rating_bar.setRating(model.googleRating.toFloat())
        holder.milesTextView.setText(model.diatance + " Miles")
        if (model.fav_status == 1) {
            holder.saveImageView.setImageDrawable(context.getDrawable(R.drawable.save))
        } else {
            holder.saveImageView.setImageDrawable(context.getDrawable(R.drawable.saved_un))
        }
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                (context as ProfileActivity).dispatchToDetailsActivity(model.businessId)
            }
        })
        holder.saveImageView.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                (context as ProfileActivity).goForAddFavoriteService(
                    model.businessId.toString()
                )
            }
        })
        holder.milesTextView.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                context.startActivity(Intent(context, DummyMapActivity::class.java))
            }
        })
    }

    public override fun getItemCount(): Int {
        return myClaimBusinessList.size
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagesRecyclerView: RecyclerView
        var saveImageView: ImageView
        var milesTextView: TextView
        var google_rating_bar: RatingBar
        var yelp_rating_bar: RatingBar? = null
        var businessName_tv: TextView
        var cuisine_tv: TextView? = null
        var address_tv: TextView? = null
        var hh_today_tv: TextView
        var lm_today_tv: TextView
        var drinks_tv: TextView
        var food_tv: TextView
        var drink_tv: TextView
        var food_tvs: TextView

        init {
            imagesRecyclerView =
                itemView.findViewById<View>(R.id.imagesRecyclerView) as RecyclerView
            saveImageView = itemView.findViewById<View>(R.id.saveImageView) as ImageView
            milesTextView = itemView.findViewById<View>(R.id.milesTextView) as TextView
            businessName_tv = itemView.findViewById<View>(R.id.businessName_tv) as TextView
            //            address_tv = (TextView) itemView.findViewById(R.id.address_tv);
//            cuisine_tv = (TextView) itemView.findViewById(R.id.cuisine_tv);
            hh_today_tv = itemView.findViewById<View>(R.id.hh_today_tv) as TextView
            lm_today_tv = itemView.findViewById<View>(R.id.lm_today_tv) as TextView
            //            yelp_rating_bar = (RatingBar) itemView.findViewById(R.id.yelp_rating_bar);
            google_rating_bar = itemView.findViewById<View>(R.id.google_rating_bar) as RatingBar
            drink_tv = itemView.findViewById<View>(R.id.drink_tv) as TextView
            food_tvs = itemView.findViewById<View>(R.id.food_tvs) as TextView
            drinks_tv = itemView.findViewById<View>(R.id.drinks_tv) as TextView
            food_tv = itemView.findViewById<View>(R.id.food_tv) as TextView
        }

        fun setRecipesImagesList(imagesList: List<String>) {
            val adapter: RecipesImagesAdapter = RecipesImagesAdapter(context, imagesList)
            val llm: LinearLayoutManager = LinearLayoutManager(context)
            llm.setOrientation(LinearLayoutManager.HORIZONTAL)
            imagesRecyclerView.setLayoutManager(llm)
            imagesRecyclerView.setAdapter(adapter)
        }
    }
}