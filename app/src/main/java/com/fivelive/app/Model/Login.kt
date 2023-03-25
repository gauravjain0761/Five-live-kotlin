package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Login {
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
    var id: String? = null

    @SerializedName("image")
    var image: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("latitude")
    var latitude: Any? = null

    @SerializedName("longitude")
    var longitude: Any? = null

    @SerializedName("mobile")
    var mobile: String? = null

    @SerializedName("notification_status")
    var notificationStatus: String? = null

    @SerializedName("signup_type")
    var signupType: String? = null

    @SerializedName("social_id")
    var socialId: Any? = null

    @SerializedName("status")
    var status: Long? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("user_no")
    var userNo: Any? = null

    @SerializedName("user_type")
    @Expose
    var userType: String? = null
}