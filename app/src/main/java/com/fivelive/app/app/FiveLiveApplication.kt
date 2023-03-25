package com.fivelive.app.app

import android.app.Application
import com.fivelive.app.Model.ClaimDetail
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.formats.UnifiedNativeAd

class FiveLiveApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        /*initialize ad mob here*/MobileAds.initialize(this) { }

        /*List<String> testDeviceIds = Arrays.asList("BA0145E39DB624436A0D368FBF338EE4");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);*/
    }

    companion object {
        @get:Synchronized
        var instance: FiveLiveApplication? = null
            private set
        var claimDetail: ClaimDetail? = null
        var homeAdsList: MutableList<UnifiedNativeAd> = ArrayList()
    }
}