package com.fivelive.app.ads

import android.app.Activity
import android.util.Log
import com.fivelive.app.R
import com.fivelive.app.prefs.SharedPreferenceWriter.Companion.getInstance
import com.fivelive.app.prefs.SharedPrefsKey
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class FullScreenAds  // private constructor restricted to this class itself
private constructor() {
    private var mInterstitialAd: InterstitialAd? = null
    fun initializeInterstitialAd(activity: Activity) {
        /* 0 means unsubscibed
        1 means subscribed*/
        val planStatus = getInstance(activity).getIntValue(SharedPrefsKey.SUBSCRIBED_STATUS)
        // int planStatus = 0; // for testing purpose
        /*if plan status is 1 we are not showing ads 1 means user subscribed plan*/if (planStatus == 1) {
            return
        }
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity,
            activity.getString(R.string.production_interstitial_ad_unit_id),  // InterstitialAd.load(activity,activity.getString(R.string.interstitial_ad_unit_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.d("InterstitialAd", "onAdLoaded")
                    requestToCallbacks(activity)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.d("InterstitialAd", loadAdError.message)
                    mInterstitialAd = null
                }
            })
    }

    fun requestToCallbacks(activity: Activity?) {
        mInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.d("InterstitialAd", "The ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                Log.d("InterstitialAd", "The ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null
                Log.d("InterstitialAd", "The ad was shown.")
            }
        }
        Log.d("InterstitialAd", "loadADs: ")
        loadAds(activity)
    }

    fun loadAds(activity: Activity?) {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(activity!!)
        } else {
            Log.d("InterstitialAd", "The interstitial ad wasn't ready yet.")
        }
    }

    companion object {
        private const val TAG = "FullScreenAds"
        private var single_instance: FullScreenAds? = null

        // static method FullScreenAds create instance of Singleton class
        val instance: FullScreenAds
            get() {
                if (single_instance == null) single_instance = FullScreenAds()
                return single_instance!!
            }
    }
}