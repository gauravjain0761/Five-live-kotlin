package com.fivelive.app.Model

import com.google.gson.annotations.SerializedName

class SignUpResponse {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("register")
    var register: Register? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("login")
     var mLogin: Login? = null

}