package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Subscription {
    @SerializedName("status")
    @Expose
    var status = 0

    @SerializedName("purchase_token")
    @Expose
    var purchaseToken: String? = null
}