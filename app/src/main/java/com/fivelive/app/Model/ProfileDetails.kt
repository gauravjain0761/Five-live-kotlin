package com.fivelive.app.Model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class ProfileDetails(
    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("first_name")
    @Expose
    var firstName: String,

    @SerializedName("last_name")
    @Expose
    var lastName: String,

    @SerializedName("mobile")
    @Expose
    var mobile: String?,

    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("dob")
    @Expose
    var dob: String?,

    @SerializedName("image")
    @Expose
    var image: String,

    @SerializedName("gender")
    @Expose
    var gender: String?
) : Parcelable {


}