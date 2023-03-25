package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonBusinessResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("menue_details")
    @Expose
    var menueDetails: MenuDetails? = null

    @SerializedName("details")
    @Expose
    var details: PlanDetails? = null

    @SerializedName("business_list")
    @Expose
    var businessList: List<BusinessList> = listOf()
}