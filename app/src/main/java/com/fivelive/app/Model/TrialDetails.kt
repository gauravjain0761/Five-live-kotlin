package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TrialDetails {
    @SerializedName("trail_status")
    @Expose
    var trailStatus = 0

    @SerializedName("left_days")
    @Expose
    var leftDays: String? = null
}