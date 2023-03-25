package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse(
    @SerializedName("login")
    var login: Login,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("socialMediaLogin")
    @Expose
    var socialMediaLogin: Login? = null
)