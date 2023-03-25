package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GuestLogin {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("user_type")
    @Expose
    var userType: String? = null
}