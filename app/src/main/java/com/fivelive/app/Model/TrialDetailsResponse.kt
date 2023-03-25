package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TrialDetailsResponse(
    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("message")
    @Expose
    var message: String = "",

    @SerializedName("details")
    @Expose
    var details: TrialDetails
)