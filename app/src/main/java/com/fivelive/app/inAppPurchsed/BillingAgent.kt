package com.fivelive.app.inAppPurchsed

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import com.fivelive.app.prefs.SharedPreferenceWriter.Companion.getInstance
import com.fivelive.app.prefs.SharedPrefsKey
import com.fivelive.app.util.AppConstant
import com.fivelive.app.util.AppUtil.dismissProgressDialog
import com.fivelive.app.util.AppUtil.showErrorDialog
import com.fivelive.app.util.AppUtil.showProgressDialog
import com.google.gson.Gson
import java.util.*

class BillingAgent(var activity: Activity, var callback: BillingCallback) :
    PurchasesUpdatedListener {
    private var billingClient: BillingClient?

    //  private List<String> subscriptionSKUList = Arrays.asList("five_live_subscription_test");
    private val subscriptionSKUList =
        Arrays.asList(AppConstant.SILVER_ID, AppConstant.GOLD_ID, AppConstant.PRO_ID)
    private val subscriptionSKUDetailsList: MutableList<SkuDetails>? = ArrayList()
    fun startConnection() {
        showProgressDialog(activity)
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                val billingResponseCode = billingResult.responseCode
                Log.d(TAG, "onBillingSetupFinished: " + billingResult.responseCode)
                if (billingResponseCode == BillingClient.BillingResponseCode.OK) {
                    availableSubscription
                } else {
                    startConnection()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "onBillingServiceDisconnected: ")
                startConnection()
            }
        })
    }

    /*for (int i = 0; i < skuDetailsList.size(); i++) {
                                InAppDataModel dataModel = new Gson().fromJson(skuDetailsList.get(i).getOriginalJson(), InAppDataModel.class);
                                callback.onBillingPriceUpdate(dataModel);
                            }*/
    val availableSubscription: Unit
        get() {
            if (billingClient!!.isReady) {
                val skuParams = SkuDetailsParams.newBuilder()
                    .setType(BillingClient.SkuType.SUBS)
                    .setSkusList(subscriptionSKUList)
                    .build()
                billingClient!!.querySkuDetailsAsync(skuParams) { billingResult, skuDetailsList ->
                    dismissProgressDialog()
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        subscriptionSKUDetailsList!!.clear()
                        subscriptionSKUDetailsList.addAll(skuDetailsList)
                        if (skuDetailsList != null && !skuDetailsList.isEmpty()) {
                            /*for (int i = 0; i < skuDetailsList.size(); i++) {
                                        InAppDataModel dataModel = new Gson().fromJson(skuDetailsList.get(i).getOriginalJson(), InAppDataModel.class);
                                        callback.onBillingPriceUpdate(dataModel);
                                    }*/
                            callback.onBillingPriceUpdate(skuDetailsList)
                        }
                    }
                }
            }
        }
    var skuDetails: SkuDetails? = null

    init {
        billingClient = BillingClient.newBuilder(activity)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        if (!billingClient!!.isReady) {
            Log.d(TAG, "BillingClient: Start connection...")
            startConnection()
        }
    }

    fun purchasedSubscription(subscriptionID: String?) {
        val list = billingClient!!.queryPurchases(BillingClient.SkuType.SUBS).purchasesList
        //returns only the current active subscriptions. It returns a cached result immediately.
        // this check user already purchased subscription here
        val purchaseToken =
            getInstance(activity)!!.getString(SharedPrefsKey.SUBSCRIBED_PURCHASE_TOKEN)
        val planStatus = getInstance(activity)!!.getIntValue(SharedPrefsKey.SUBSCRIBED_STATUS)
        if (planStatus == 1) {
            if (list != null && list.size > 0) {
                //  Purchase purchase = list.get(0);
                Log.d(TAG, "purchasedSubscription: serverToken:$purchaseToken")
                Log.d(TAG, "purchasedSubscription: googleplaytoken:$purchaseToken")
                var alreadySubscribe = false
                for (purchase in list) {
                    if (purchaseToken != null && purchase.purchaseToken == purchaseToken) {
                        alreadySubscribe = true
                    }
                }
                if (alreadySubscribe) {
                    callback.onTokenConsumed()
                } else {
                    launchBillingFLow(subscriptionID)
                }


                /*if (purchaseToken != null && purchase.getPurchaseToken().equals(purchaseToken)) {
                    callback.onTokenConsumed();
                } else {
                    // means new user credintial use
                    launchBillingFLow(subscriptionID);
                }*/
            } else {
                // means plan expired
                launchBillingFLow(subscriptionID)
            }
        } else {
            // means user till not subscribed
            launchBillingFLow(subscriptionID)
        }
    }

    fun launchBillingFLow(subscriptionID: String?) {
        if (subscriptionSKUDetailsList != null && !subscriptionSKUDetailsList.isEmpty()) {
            for (i in subscriptionSKUDetailsList.indices) {
                val dataModel = Gson().fromJson(
                    subscriptionSKUDetailsList[i].originalJson,
                    InAppDataModel::class.java
                )
                if (dataModel.productId.equals(subscriptionID, ignoreCase = true)) {
                    skuDetails = subscriptionSKUDetailsList[i]
                } else if (dataModel.productId.equals(subscriptionID, ignoreCase = true)) {
                    skuDetails = subscriptionSKUDetailsList[i]
                } else if (dataModel.productId.equals(subscriptionID, ignoreCase = true)) {
                    skuDetails = subscriptionSKUDetailsList[i]
                }
            }
            if (skuDetails != null) {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails!!)
                    .build()
                val responseCode =
                    billingClient!!.launchBillingFlow(activity, billingFlowParams).responseCode
                Log.w("testPurchase", "respCode: $responseCode")
            }
        } else {
            startConnection()
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, list: List<Purchase>?) {
        checkSubscription(billingResult, list)
    }

    private fun checkSubscription(billingResult: BillingResult, list: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
            val purchase = list[0]
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient!!.acknowledgePurchase(acknowledgePurchaseParams) {
                        callback.onPurchaseCompleted(
                            purchase
                        )
                    }
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
            Log.d(TAG, "service disconnected")
            startConnection()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d(TAG, "USER_CANCELED")
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            // Handle an error caused by a user alreay subscribe plan the purchase flow.
            showErrorDialog(
                activity,
                "Alert!",
                "You can't subscribe the same plan from the same play store account."
            )
            Log.d(TAG, "ITEM_ALREADY_OWNED")
        } else {
            Log.d(TAG, "UpdateListener:code:" + billingResult.responseCode)
        }
    }

    fun onDestroy() {
        if (billingClient != null && billingClient!!.isReady) {
            billingClient!!.endConnection()
            billingClient = null
        }
    }

    companion object {
        private const val TAG = "BillingAgent"
    }
}