package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ClaimBusinessResponse {
    @SerializedName("status")
    @Expose
    var status: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("claim_business")
    @Expose
    var claimBusiness: ClaimBusiness? = null
}