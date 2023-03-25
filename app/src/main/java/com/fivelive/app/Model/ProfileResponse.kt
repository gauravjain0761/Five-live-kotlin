package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("profile_details")
    @Expose
    var profileDetails: ProfileDetails? = null

    @SerializedName("my_claim_business")
    @Expose
    var myClaimBusiness: List<MyClaimBusiness> = listOf()
}