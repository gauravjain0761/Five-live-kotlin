package com.fivelive.app.adapter

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.fivelive.app.Model.HomeBusiness
import com.fivelive.app.R
import com.fivelive.app.activity.HomeActivity
import com.fivelive.app.ads.GoogleAdsHelper
import com.fivelive.app.ads.UnifiedNativeAdViewHolder
import com.fivelive.app.diffUtill.BusinessAdapterDiffUtil
import com.fivelive.app.fragment.HappeningNowFragment
import com.fivelive.app.fragment.HomeFragment
import com.fivelive.app.fragment.NewHomeFragment
import com.fivelive.app.fragment.SavedFragment
import com.fivelive.app.readMoreFunctionality.ExpandableTextView
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil
import com.google.android.gms.ads.formats.UnifiedNativeAd
import java.util.*

class BusinessListOrAdsAdapter     //  this.businessList = businessList;
//  listCopy.addAll(businessList);
constructor(
    var context: Context,
    private var mRecyclerViewItems: MutableList<Any>,
    var fragment: Fragment,
    var staticFilter: String,
    var recycledViewPool: RecycledViewPool
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var businessList: MutableList<HomeBusiness>? = null
    var listCopy: List<HomeBusiness> = ArrayList()
    var businessCount: Int = 0
    public override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when (viewType) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val unifiedNativeLayoutView: View = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.ad_unified, viewGroup, false)
                return UnifiedNativeAdViewHolder(unifiedNativeLayoutView)
            }
            LOADING_VIEW_TYPE -> {
                val view: View = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_loading, viewGroup, false)
                return LoadingViewHolder(view)
            }
            MENU_ITEM_VIEW_TYPE -> {
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view1: View =
                    inflater.inflate(R.layout.restaurant_details_lauout_new, viewGroup, false)
                return MyViewHolder(view1)
            }
            else -> {
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view1: View =
                    inflater.inflate(R.layout.restaurant_details_lauout_new, viewGroup, false)
                return MyViewHolder(view1)
            }
        }
    }

    public override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType: Int = getItemViewType(position)
        Log.d("changedPosition", "onBindViewHolder: " + position)
        when (viewType) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val nativeAd: UnifiedNativeAd = mRecyclerViewItems.get(position) as UnifiedNativeAd
                GoogleAdsHelper.populateNativeAdView(
                    nativeAd,
                    (holder as UnifiedNativeAdViewHolder).adView
                )
            }
            LOADING_VIEW_TYPE -> showLoadingView()
            MENU_ITEM_VIEW_TYPE -> {
                val myViewHolder: MyViewHolder = holder as MyViewHolder
                val model: HomeBusiness = mRecyclerViewItems.get(position) as HomeBusiness
                Log.d("sanjuTest", "onBindViewHolder: $businessCount")
                if (model.businessCount == 0) {
                    businessCount++
                    model.businessCount = businessCount
                }
                myViewHolder.tvDollar.text = model.price
                //  myViewHolder.imagesRecyclerView.setRecycledViewPool(recycledViewPool);
                myViewHolder.setRecipesImagesList(model.images)
                if (model.hh_drink != null && !(model.hh_drink == "")) {
                    myViewHolder.drinks_tv.setText(Html.fromHtml(model.hh_drink))
                    // myViewHolder.drinks_tv.setText(Html.fromHtml(context.getString(R.string.dummy_text)));
                    if (model.hh_drink!!.length > AppConstant.EXPENDABLE_TEXT_LENGTH) {
                        ExpandableTextView.makeTextViewResizable(
                            myViewHolder.drinks_tv,
                            2,
                            AppConstant.SEE_MORE,
                            true
                        )
                    }
                } else {
                    myViewHolder.drinks_tv.setVisibility(View.GONE)
                    myViewHolder.drink_tv.setVisibility(View.GONE)
                }
                if (model.hh_food != null && !(model.hh_food == "")) {
                    myViewHolder.food_tv.setText(Html.fromHtml(model.hh_food))
                    if (model.hh_food!!.length > AppConstant.EXPENDABLE_TEXT_LENGTH) {
                        ExpandableTextView.makeTextViewResizable(
                            myViewHolder.food_tv,
                            2,
                            AppConstant.SEE_MORE,
                            true
                        )
                    }
                } else {
                    myViewHolder.food_tv.setVisibility(View.GONE)
                    myViewHolder.food_tvs.setVisibility(View.GONE)
                }
                myViewHolder.businessName_tv.setText(
                    model.businessCount.toString() + ". " + model.name
                )
                AppUtil.setHappyHoursDayOrTime(
                    myViewHolder.hh_today_tv,
                    model.current_happy_hours,
                    model.hhHoursStartTime,
                    model.hhHoursEndTime,
                    staticFilter
                )
                AppUtil.setMusicLiveDayOrTime(
                    myViewHolder.lm_today_tv,
                    model.current_happy_hours,
                    model.musicStartTime,
                    model.musicEndTime
                )
                myViewHolder.google_rating_bar.setRating(model.googleRating.toFloat())
                myViewHolder.milesTextView.setText(model.diatance + " " + AppConstant.MILES)
                if (model.favStatus == 1) {
                    myViewHolder.saveImageView.setImageDrawable(context.getDrawable(R.drawable.save))
                } else {
                    myViewHolder.saveImageView.setImageDrawable(context.getDrawable(R.drawable.unsaved))
                }
                myViewHolder.itemView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(view: View) {
                        (context as HomeActivity).dispatchToDetailsActivity(model.id)
                        // ((NewHomeFragment) fragment).dispatchToDetailsActivity(model.getId());
                    }
                })
                myViewHolder.saveImageView.setFocusableInTouchMode(true)
                myViewHolder.saveImageView.setOnFocusChangeListener(object : OnFocusChangeListener {
                    public override fun onFocusChange(v: View, hasFocus: Boolean) {
                        if (hasFocus) {
                            myViewHolder.saveImageView.performClick()
                        }
                    }
                })
                myViewHolder.saveImageView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(view: View) {
                        if (AppUtil.checkUserType(context)) {
                            AppUtil.showGuestLoginDialog(context)
                            return
                        }
                        if (fragment is NewHomeFragment) {
                            (fragment as NewHomeFragment).goForAddFavoriteService(
                                model.id.toString(), position, model
                            )
                        } else if (fragment is SavedFragment) {
                            (fragment as SavedFragment).goForAddFavoriteService(
                                model.id.toString(), position
                            )
                        } else if (fragment is HomeFragment) {
                            (fragment as HappeningNowFragment).goForAddFavoriteService(
                                model.id.toString(), position
                            )
                        }
                    }
                })
                myViewHolder.milesTextView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(view: View) {
                        when (fragment) {
                            is NewHomeFragment -> {
                                (fragment as NewHomeFragment).dispatchToGoogleMap(
                                    model.latitude,
                                    model.longitude
                                )
                            }
                            is SavedFragment -> {
                                (fragment as SavedFragment).dispatchToGoogleMap(
                                    model.latitude,
                                    model.longitude
                                )
                            }
                            is HappeningNowFragment -> {
                                (fragment as HappeningNowFragment).dispatchToGoogleMap(
                                    model.latitude,
                                    model.longitude
                                )
                            }
                        }
                    }
                })

            }
            else -> {
                val myViewHolder: MyViewHolder = holder as MyViewHolder
                val model: HomeBusiness = mRecyclerViewItems.get(position) as HomeBusiness
                Log.d("sanjuTest", "onBindViewHolder: " + businessCount)
                if (model.businessCount == 0) {
                    businessCount++
                    model.businessCount = businessCount
                }
                myViewHolder.setRecipesImagesList(model.images)
                if (model.hh_drink != null && !(model.hh_drink == "")) {
                    myViewHolder.drinks_tv.setText(Html.fromHtml(model.hh_drink))
                    if (model.hh_drink.length > AppConstant.EXPENDABLE_TEXT_LENGTH) {
                        ExpandableTextView.makeTextViewResizable(
                            myViewHolder.drinks_tv,
                            2,
                            AppConstant.SEE_MORE,
                            true
                        )
                    }
                } else {
                    myViewHolder.drinks_tv.setVisibility(View.GONE)
                    myViewHolder.drink_tv.setVisibility(View.GONE)
                }
                if (model.hh_food != null && !(model.hh_food == "")) {
                    myViewHolder.food_tv.setText(Html.fromHtml(model.hh_food))
                    if (model.hh_food.length > AppConstant.EXPENDABLE_TEXT_LENGTH) {
                        ExpandableTextView.makeTextViewResizable(
                            myViewHolder.food_tv,
                            2,
                            AppConstant.SEE_MORE,
                            true
                        )
                    }
                } else {
                    myViewHolder.food_tv.setVisibility(View.GONE)
                    myViewHolder.food_tvs.setVisibility(View.GONE)
                }
                myViewHolder.businessName_tv.setText(
                    model.businessCount.toString() + ". " + model.name
                )
                AppUtil.setHappyHoursDayOrTime(
                    myViewHolder.hh_today_tv,
                    model.current_happy_hours,
                    model.hhHoursStartTime,
                    model.hhHoursEndTime,
                    staticFilter
                )
                AppUtil.setMusicLiveDayOrTime(
                    myViewHolder.lm_today_tv,
                    model.current_happy_hours,
                    model.musicStartTime,
                    model.musicEndTime
                )
                myViewHolder.google_rating_bar.setRating(model.googleRating.toFloat())
                myViewHolder.milesTextView.setText(model.diatance + " " + AppConstant.MILES)
                if (model.favStatus == 1) {
                    myViewHolder.saveImageView.setImageDrawable(context.getDrawable(R.drawable.save))
                } else {
                    myViewHolder.saveImageView.setImageDrawable(context.getDrawable(R.drawable.unsaved))
                }
                myViewHolder.itemView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(view: View) {
                        (context as HomeActivity).dispatchToDetailsActivity(model.id)
                    }
                })
                myViewHolder.saveImageView.setFocusableInTouchMode(true)
                myViewHolder.saveImageView.setOnFocusChangeListener(object : OnFocusChangeListener {
                    public override fun onFocusChange(v: View, hasFocus: Boolean) {
                        if (hasFocus) {
                            myViewHolder.saveImageView.performClick()
                        }
                    }
                })
                myViewHolder.saveImageView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(view: View) {
                        if (AppUtil.checkUserType(context)) {
                            AppUtil.showGuestLoginDialog(context)
                            return
                        }
                        if (fragment is NewHomeFragment) {
                            (fragment as NewHomeFragment).goForAddFavoriteService(
                                model.id.toString(), position, model
                            )
                        } else if (fragment is SavedFragment) {
                            (fragment as SavedFragment).goForAddFavoriteService(
                                model.id.toString(), position
                            )
                        } else if (fragment is HomeFragment) {
                            (fragment as HappeningNowFragment).goForAddFavoriteService(
                                model.id.toString(), position
                            )
                        }
                    }
                })
                myViewHolder.milesTextView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(view: View) {
                        if (fragment is NewHomeFragment) {
                            (fragment as NewHomeFragment).dispatchToGoogleMap(
                                model.latitude,
                                model.longitude
                            )
                        } else if (fragment is SavedFragment) {
                            (fragment as SavedFragment).dispatchToGoogleMap(
                                model.latitude,
                                model.longitude
                            )
                        } else if (fragment is HappeningNowFragment) {
                            (fragment as HappeningNowFragment).dispatchToGoogleMap(
                                model.latitude,
                                model.longitude
                            )
                        }
                    }
                })
            }
        }
    }

    fun updateList() {
        businessCount = 0
        mRecyclerViewItems = ArrayList()
        notifyDataSetChanged()
    }

    fun removeFromList(id: String) {
        for (business: HomeBusiness in businessList!!) {
            if ((business.id == id)) {
                businessList!!.remove(business)
                break
            }
        }
        notifyDataSetChanged()
    }

    fun diffUtilChangeData(mRecyclerViewItems: List<Any>?) {
        val objectList: MutableList<Any> = ArrayList()
        objectList.addAll((mRecyclerViewItems)!!)
        val diffUtil: BusinessAdapterDiffUtil =
            BusinessAdapterDiffUtil(this.mRecyclerViewItems, objectList)
        val diffResult: DiffResult = DiffUtil.calculateDiff(diffUtil)
        businessCount = 0
        this.mRecyclerViewItems.clear()
        this.mRecyclerViewItems.addAll(objectList)
        diffResult.dispatchUpdatesTo(this)
    }

    public override fun getItemCount(): Int {
        return mRecyclerViewItems.size
        // return 5;
    }

    public override fun getItemViewType(position: Int): Int {
        val recyclerViewItem: Any = mRecyclerViewItems.get(position)
        if (recyclerViewItem is UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE
        } else if (recyclerViewItem is HomeBusiness) {
            return MENU_ITEM_VIEW_TYPE
        } else {
            return LOADING_VIEW_TYPE
        }
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
        var tvDollar: TextView
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
            tvDollar = itemView.findViewById<View>(R.id.tvDollar) as TextView
        }

        fun setRecipesImagesList(imagesList: List<String>) {
            imagesRecyclerView.setLayoutManager(
                LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            imagesRecyclerView.setAdapter(RecipesImagesAdapter(context, imagesList))
        }
    }

    private inner class LoadingViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

    private fun showLoadingView() {
        //ProgressBar would be displayed
    }

    fun filter(text: String) {
        var text: String = text
        if (text.isEmpty()) {
            businessList!!.clear()
            businessList!!.addAll(listCopy)
        } else {
            val result: ArrayList<HomeBusiness> = ArrayList()
            text = text.lowercase(Locale.getDefault())
            for (item: HomeBusiness in listCopy) {
                val fullName: String = item.name.lowercase(Locale.getDefault())
                if (fullName.contains(text)) {
                    result.add(item)
                }
            }
            businessList!!.clear()
            businessList!!.addAll(result)
        }
        notifyDataSetChanged()
    }

    companion object {
        private val MENU_ITEM_VIEW_TYPE: Int = 0
        private val UNIFIED_NATIVE_AD_VIEW_TYPE: Int = 1
        private val LOADING_VIEW_TYPE: Int = 2
    }
}