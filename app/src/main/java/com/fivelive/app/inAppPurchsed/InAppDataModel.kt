package com.fivelive.app.inAppPurchsed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InAppDataModel {
    @SerializedName("skuDetailsToken")
    @Expose
    var skuDetailsToken: String? = null

    @SerializedName("productId")
    @Expose
    var productId: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("price_amount_micros")
    @Expose
    var priceAmountMicros: String? = null

    @SerializedName("price_currency_code")
    @Expose
    var priceCurrencyCode: String? = null

    @SerializedName("subscriptionPeriod")
    @Expose
    var subscriptionPeriod: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null
}