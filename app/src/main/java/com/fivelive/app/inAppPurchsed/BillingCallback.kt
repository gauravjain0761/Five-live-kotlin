package com.fivelive.app.inAppPurchsed

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

interface BillingCallback {
    fun onTokenConsumed()
    fun onBillingPriceUpdate(skuDetailsList: List<SkuDetails>?)
    fun onPurchaseCompleted(purchase: Purchase?)
}