package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeBusinessResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("home_business")
    @Expose
    var homeBusiness: MutableList<HomeBusiness> = mutableListOf()

    @SerializedName("my_favorite")
    @Expose
    var myFavorite: List<HomeBusiness> = listOf()

    @SerializedName("claim_detail")
    @Expose
    var claimDetail: ClaimDetail? = null

    @SerializedName("subscription")
    @Expose
    var subscription: Subscription? = null

    @SerializedName("happening_now")
    @Expose
    var happeningNow: List<HomeBusiness> = listOf()

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("requestKey")
    @Expose
    var requestKey: String? = null
}