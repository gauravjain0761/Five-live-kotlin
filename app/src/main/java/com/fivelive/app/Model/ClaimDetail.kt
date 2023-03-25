package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ClaimDetail {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("google_rating")
    @Expose
    var googleRating: String? = null

    @SerializedName("yelp_rating")
    @Expose
    var yelpRating: String? = null

    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("category")
    @Expose
    var category: List<String> = listOf()

    @SerializedName("image")
    @Expose
    var image: String? = null
}