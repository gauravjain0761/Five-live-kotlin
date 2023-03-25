package com.fivelive.app.billing

import android.app.Activity
import com.android.billingclient.api.*

class BillingAgent(val activity: Activity, val callback: BillingCallback) : PurchasesUpdatedListener {


    private var billingClient = BillingClient.newBuilder(activity).setListener(this).enablePendingPurchases()
            .build()

    private val productsSKUList = listOf("id of product")
    private val productsList = arrayListOf<SkuDetails>()

    private val subscriptionSKUList = listOf("five_live_subscription_test")
    private val subscriptionList = arrayListOf<SkuDetails>()


    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                   // getAvailableProduct() // this for in consumed product
                    getAvailableSubscription(); // this for subscription
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchase: MutableList<Purchase>?) {
      //  TODO("Not yet implemented")
      //  checkProduct(billingResult, purchase);//For consumedable product
        checkSubscription(billingResult, purchase);
    }

    fun checkProduct(billingResult: BillingResult, purchase: MutableList<Purchase>?) {
        //Todo for managed peoduct means consumedable product
    }

   fun checkSubscription(billingResult: BillingResult, purchase: MutableList<Purchase>?){
       if (billingResult.responseCode == BillingClient.BillingResponseCode.OK ||
               billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
           callback.onTokenConsumed()
       }
    }

    fun getAvailableProduct() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(productsSKUList).setType(BillingClient.SkuType.INAPP).build()
            billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                // Process the result.
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productsList.clear()
                    if (skuDetailsList != null) {
                        productsList.addAll(skuDetailsList)
                    };
                }
            }
        }
    }

    fun getAvailableSubscription() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(subscriptionSKUList).setType(BillingClient.SkuType.SUBS).build()
            billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                // Process the result.
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    subscriptionList.clear()
                    if (skuDetailsList != null) {
                        subscriptionList.addAll(skuDetailsList)
                    };
                }
            }
        }
    }

    fun onDestroy() {
        billingClient.endConnection()
    }

    fun purchasedView() {
        if (productsList.size > 0) {
            val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(productsList[0])
                    .build()

            val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode
        }
    }

    fun purchasedSubscription() {
        val list = billingClient.queryPurchases(BillingClient.SkuType.SUBS).purchasesList
        // this check user already purchased subscription here
        if (list!!.size > 0) {
            callback.onTokenConsumed()
        } else {
            if (subscriptionList.size > 0) {
                val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(subscriptionList[0])
                        .build()

                val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode

            }
        }
    }
}