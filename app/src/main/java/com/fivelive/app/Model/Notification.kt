package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Notification {
    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status")
    @Expose
    var status = 0

    @SerializedName("time")
    @Expose
    var time: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
}