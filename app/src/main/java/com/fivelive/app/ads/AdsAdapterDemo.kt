package com.fivelive.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
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
import com.fivelive.app.adapter.RecipesImagesAdapter
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import java.util.*

class AdsAdapterDemo(
    var context: Context,
    var businessList: List<HomeBusiness>,
    fragment: Fragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listCopy: MutableList<HomeBusiness> = ArrayList()
    var fragment: Fragment
    var businessCount = 1

    init {
        listCopy.addAll(businessList)
        this.fragment = fragment
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        /* if (viewType == AD_TYPE) {
            adViewHolder madViewHolder = new adViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_template, null, false));
            return madViewHolder;
        } else {
            MyViewHolder mYourViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_details_lauout_new, null, false));
            return mYourViewHolder;
        }*/
        return when (viewType) {
            AD_TYPE -> {
                val adview = LayoutInflater.from(parent.context).inflate(
                    R.layout.ad_template,
                    parent, false
                )
                adViewHolder(adview)
            }
            ITEM_VIEW_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.restaurant_details_lauout_new, parent, false)
                MyViewHolder(view)
            }
            else -> {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.restaurant_details_lauout_new, parent, false)
                MyViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == ITEM_VIEW_TYPE) {
            //Todo set data in list
        } else if (viewType == AD_TYPE) {
            refreshAd((holder as adViewHolder).Adtemplate)
            /*final AdLoader adLoader = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/8135179316")
                    .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            // Show the ad.
                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().build();
                            TemplateView template = ((adViewHolder) holder).Adtemplate;
                            template.setStyles(styles);
                            template.setNativeAd(unifiedNativeAd);

                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            // Handle the failure by logging, altering the UI, and so on.
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());*/
        }
    }

    override fun getItemCount(): Int {
        return 50
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("", "getItemViewType: ")
        return if (position != 0 && position % 10 == 0) {
            AD_TYPE
        } else ITEM_VIEW_TYPE
    }

    internal inner class adViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var Adtemplate: TemplateView

        init {
            Adtemplate = itemView.findViewById(R.id.my_template)
        }
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

        init {
            imagesRecyclerView =
                itemView.findViewById<View>(R.id.imagesRecyclerView) as RecyclerView
            saveImageView = itemView.findViewById<View>(R.id.saveImageView) as ImageView
            milesTextView = itemView.findViewById<View>(R.id.milesTextView) as TextView
            businessName_tv = itemView.findViewById<View>(R.id.businessName_tv) as TextView
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

    fun refreshAd(template: TemplateView) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                (context as Activity).runOnUiThread {
                    val adLoader = AdLoader.Builder(
                        context, "ca-app-pub-3940256099942544/2247696110"
                    )
                        .forUnifiedNativeAd { unifiedNativeAd ->
                            val styles = NativeTemplateStyle.Builder()
                                .build /*withMainBackgroundColor(background).*/()
                            template.setStyles(styles)
                            template.setNativeAd(unifiedNativeAd)
                            template.visibility = View.VISIBLE
                        }
                        .build()
                    adLoader.loadAd(AdRequest.Builder().build())
                }
            }
        }, 200)
    }

    companion object {
        private const val ITEM_VIEW_TYPE = 0
        private const val AD_TYPE = 1
    }
}