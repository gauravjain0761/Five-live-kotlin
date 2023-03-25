package com.fivelive.app.ads

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.fivelive.app.R
import com.fivelive.app.adapter.BusinessListOrAdsAdapter
import com.fivelive.app.app.FiveLiveApplication
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView

class GoogleAdsHelper(
    var context: Context,
    var listOrAdsAdapter: BusinessListOrAdsAdapter,
    var mRecyclerViewItemsList: List<Any>
) {
    // The AdLoader used to load ads.
    private var adLoader: AdLoader? = null

    // private List<Object> mRecyclerViewItemsList = new ArrayList<>();
    private val mNativeAdsList: MutableList<UnifiedNativeAd> = ArrayList()
    fun loadNativeAds() {
        if (FiveLiveApplication.homeAdsList.size != 5) {
            val builder = AdLoader.Builder(
                context, context.getString(R.string.testing_ad_mob_app_id)
            )
            adLoader =
                builder.forUnifiedNativeAd { unifiedNativeAd -> // A native ad loaded successfully, check if the ad loader has finished loading
                    // and if so, insert the ads into the list.
                    mNativeAdsList.add(unifiedNativeAd)
                    FiveLiveApplication.homeAdsList.addAll(mNativeAdsList)
                    if (!adLoader!!.isLoading) {
                        insertAdsInMenuItems()
                    }
                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e(
                            "MainActivity",
                            "The previous native ad failed to load. Attempting to" + " load another."
                        )
                        if (!adLoader!!.isLoading) {
                            insertAdsInMenuItems()
                        }
                    }
                }).build()
            // Load the Native ads.
            adLoader?.loadAds(AdRequest.Builder().build(), 5)
        } else {
            mNativeAdsList.addAll(FiveLiveApplication.homeAdsList)
            insertAdsInMenuItems()
        }
    }

    private fun insertAdsInMenuItems() {
        val itemOrAdsList: MutableList<Any> = ArrayList()
        itemOrAdsList.addAll(mRecyclerViewItemsList)
        if (mNativeAdsList.size <= 0) {
            return
        }

        /*int offset = (itemOrAdsList.size() % mNativeAdsList.size()) + 1;
        int index = 0;
        for (UnifiedNativeAd ad : mNativeAdsList) {

            itemOrAdsList.add(index, ad);
            index = index + offset;
            if(index > itemOrAdsList.size()){
                break;
            }*/
        val offset = itemOrAdsList.size % mNativeAdsList.size + 1
        var index = 0
        for (ad in mNativeAdsList) {
            index += 5
            if (index < itemOrAdsList.size) {
                itemOrAdsList.add(index, ad)
            }
            if (index > itemOrAdsList.size) {
                break
            }
        }
        // Todo after loading ads notifiy data set change methods call
        // listOrAdsAdapter.notifyDataSetChanged();
        listOrAdsAdapter.diffUtilChangeData(itemOrAdsList)
        //  listOrAdsAdapter.notifyItemRangeChanged(0,5);
    }

    companion object {
        //The loadAds() method sends a request for multiple ads (up to 5):
        // The number of native ads to load.
        const val NUMBER_OF_ADS = 5
        fun populateNativeAdView(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
            // Some assets are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            (adView.bodyView as TextView).text = nativeAd.body
            (adView.callToActionView as Button).text = nativeAd.callToAction

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            val icon = nativeAd.icon
            if (icon == null) {
                adView.iconView.visibility = View.INVISIBLE
            } else {
                (adView.iconView as ImageView).setImageDrawable(icon.drawable)
                adView.iconView.visibility = View.VISIBLE
            }
            if (nativeAd.price == null) {
                adView.priceView.visibility = View.INVISIBLE
            } else {
                adView.priceView.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }
            if (nativeAd.store == null) {
                adView.storeView.visibility = View.INVISIBLE
            } else {
                adView.storeView.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }
            if (nativeAd.starRating == null) {
                adView.starRatingView.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
                adView.starRatingView.visibility = View.VISIBLE
            }
            if (nativeAd.advertiser == null) {
                adView.advertiserView.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView.visibility = View.VISIBLE
            }

            // Assign native ad object to the native view.
            adView.setNativeAd(nativeAd)
        }
    }
}