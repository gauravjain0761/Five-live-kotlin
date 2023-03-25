package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ItemDetailsResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("ite_details")
    @Expose
    var iteDetails: CategoryData? = null
}