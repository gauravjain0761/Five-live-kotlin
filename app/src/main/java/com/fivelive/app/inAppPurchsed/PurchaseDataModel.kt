package com.fivelive.app.inAppPurchsed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PurchaseDataModel {
    @SerializedName("orderId")
    @Expose
    var orderId: String? = null

    @SerializedName("packageName")
    @Expose
    var packageName: String? = null

    @SerializedName("productId")
    @Expose
    var productId: String? = null

    @SerializedName("purchaseTime")
    @Expose
    var purchaseTime: String? = null

    @SerializedName("purchaseState")
    @Expose
    var purchaseState: String? = null

    @SerializedName("purchaseToken")
    @Expose
    var purchaseToken: String? = null

    @SerializedName("autoRenewing")
    @Expose
    var autoRenewing = false

    @SerializedName("acknowledged")
    @Expose
    var acknowledged = false
}