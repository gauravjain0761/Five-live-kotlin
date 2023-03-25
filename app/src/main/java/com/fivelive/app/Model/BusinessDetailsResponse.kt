package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BusinessDetailsResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("business_details")
    @Expose
    var businessDetails: BusinessDetails? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("requestKey")
    @Expose
    var requestKey: String? = null
}