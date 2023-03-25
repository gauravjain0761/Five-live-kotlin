package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HappyData {
    @SerializedName("days")
    @Expose
    var days: String? = null

    @SerializedName("start_time")
    @Expose
    var startTime: String? = null

    @SerializedName("end_time")
    @Expose
    var endTime: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null
}