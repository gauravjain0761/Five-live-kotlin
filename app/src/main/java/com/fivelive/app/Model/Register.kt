package com.fivelive.app.Model

import com.google.gson.annotations.SerializedName

class Register {
    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("device_token")
    var deviceToken: String? = null

    @SerializedName("device_type")
    var deviceType: String? = null

    @SerializedName("dob")
    var dob: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("mobile")
    var mobile: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}