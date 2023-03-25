package com.fivelive.app.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivelive.app.Model.HomeBusiness
import com.fivelive.app.R
import com.fivelive.app.activity.HomeActivity
import com.fivelive.app.fragment.HappeningNowFragment
import com.fivelive.app.fragment.HomeFragment
import com.fivelive.app.fragment.SavedFragment
import com.fivelive.app.readMoreFunctionality.ExpandableTextView
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import java.util.*

class RestaurantListAdapter(
    var context: Context,
    var businessList: MutableList<HomeBusiness>,
    fragment: Fragment
) : RecyclerView.Adapter<RestaurantListAdapter.MyViewHolder>() {
    var listCopy: MutableList<HomeBusiness> = ArrayList()
    var fragment: Fragment
    var businessCount = 1

    init {
        listCopy.addAll(businessList)
        this.fragment = fragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.restaurant_details_lauout_new, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = businessList[position]
        holder.setRecipesImagesList(model.images)
        if (model.hh_drink != null && model.hh_drink != "") {
            holder.drinks_tv.text = Html.fromHtml(model.hh_drink)
            if (model.hh_drink.length > AppConstant.EXPENDABLE_TEXT_LENGTH) {
                ExpandableTextView.makeTextViewResizable(
                    holder.drinks_tv,
                    2,
                    AppConstant.SEE_MORE,
                    true
                )
            }
        } else {
            holder.drinks_tv.visibility = View.GONE
            holder.drink_tv.visibility = View.GONE
        }
        if (model.hh_food != null && model.hh_food != "") {
            holder.food_tv.text = Html.fromHtml(model.hh_food)
            if (model.hh_food.length > AppConstant.EXPENDABLE_TEXT_LENGTH) {
                ExpandableTextView.makeTextViewResizable(
                    holder.food_tv,
                    2,
                    AppConstant.SEE_MORE,
                    true
                )
            }
        } else {
            holder.food_tv.visibility = View.GONE
            holder.food_tvs.visibility = View.GONE
        }
        holder.businessName_tv.text = (businessCount + position).toString() + ". " + model.name
        //  holder.address_tv.setText(model.getAddress());
        //  holder.cuisine_tv.setText(AppUtil.getCuisineString(model.getCuisines()));
        AppUtil.setHappyHoursTime(holder.hh_today_tv, model.hhHoursStartTime, model.hhHoursEndTime)
        AppUtil.setMusicLiveTime(holder.lm_today_tv, model.musicStartTime, model.musicEndTime)
        //  holder.yelp_rating_bar.setRating(Float.parseFloat(model.getYelpRating()));
        holder.google_rating_bar.rating = model.googleRating.toFloat()
        holder.milesTextView.text = model.diatance + " Miles"
        if (model.favStatus == 1) {
            holder.saveImageView.setImageDrawable(context.getDrawable(R.drawable.save))
        } else {
            holder.saveImageView.setImageDrawable(context.getDrawable(R.drawable.unsaved))
        }
        holder.itemView.setOnClickListener {
            (context as HomeActivity).dispatchToDetailsActivity(
                model.id
            )
        }
        holder.saveImageView.setOnClickListener(View.OnClickListener { // here we are checking user type. if user type guest we disable this functionality
            if (AppUtil.checkUserType(context)) {
                AppUtil.showGuestLoginDialog(context)
                return@OnClickListener
            }
            if (fragment is HomeFragment) {
                (fragment as HomeFragment).goForAddFavoriteService(model.id.toString())
            } else if (fragment is SavedFragment) {
                (fragment as SavedFragment).goForAddFavoriteService(model.id.toString(), position)
            } else if (fragment is HappeningNowFragment) {
                (fragment as HappeningNowFragment).goForAddFavoriteService(
                    model.id.toString(),
                    position
                )
            }
        })
        holder.milesTextView.setOnClickListener {
            if (fragment is HomeFragment) {
                (fragment as HomeFragment).dispatchToGoogleMap(model.latitude, model.longitude)
            } else if (fragment is SavedFragment) {
                (fragment as SavedFragment).dispatchToGoogleMap(model.latitude, model.longitude)
            } else if (fragment is HappeningNowFragment) {
                (fragment as HappeningNowFragment).dispatchToGoogleMap(
                    model.latitude,
                    model.longitude
                )
            }
        }
    }

    fun removeFromList(id: String) {
        for (business in businessList) {
            if (business.id == id) {
                businessList.remove(business)
                break
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return businessList.size
        // return 5;
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            drink_tv = itemView.findViewById<View>(R.id.drink_tv) as TextView
            food_tvs = itemView.findViewById<View>(R.id.food_tvs) as TextView
            //  address_tv = (TextView) itemView.findViewById(R.id.address_tv);
            //  cuisine_tv = (TextView) itemView.findViewById(R.id.cuisine_tv);
            hh_today_tv = itemView.findViewById<View>(R.id.hh_today_tv) as TextView
            lm_today_tv = itemView.findViewById<View>(R.id.lm_today_tv) as TextView
            // yelp_rating_bar = (RatingBar) itemView.findViewById(R.id.yelp_rating_bar);
            google_rating_bar = itemView.findViewById<View>(R.id.google_rating_bar) as RatingBar
            drinks_tv = itemView.findViewById<View>(R.id.drinks_tv) as TextView
            food_tv = itemView.findViewById<View>(R.id.food_tv) as TextView
        }

        fun setRecipesImagesList(imagesList: List<String>) {
            val adapter = RecipesImagesAdapter(context, imagesList)
            val llm = LinearLayoutManager(context)
            llm.orientation = LinearLayoutManager.HORIZONTAL
            imagesRecyclerView.layoutManager = llm
            imagesRecyclerView.adapter = adapter
        }
    }

    fun filter(text: String) {
        var text = text
        if (text.isEmpty()) {
            businessList.clear()
            businessList.addAll(listCopy)
        } else {
            val result = ArrayList<HomeBusiness>()
            text = text.lowercase(Locale.getDefault())
            for (item in listCopy) {
                val fullName = item.name.lowercase(Locale.getDefault())
                if (fullName.contains(text)) {
                    result.add(item)
                }
            }
            businessList.clear()
            businessList.addAll(result)
        }
        notifyDataSetChanged()
    }
}