package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReviewArray {
    @SerializedName("user_name")
    @Expose
    var userName: String? = null

    @SerializedName("user_image")
    @Expose
    var userImage: String? = null

    @SerializedName("rating")
    @Expose
    var rating = 0f

    @SerializedName("review")
    @Expose
    var review: String? = null

    @SerializedName("images")
    @Expose
    var images: List<String> = listOf()

    @SerializedName("time")
    @Expose
    var time: String? = null
}