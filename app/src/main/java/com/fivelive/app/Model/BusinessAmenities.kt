package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BusinessAmenities {
    @SerializedName("business_name")
    @Expose
    var businessName: String = ""

    @SerializedName("cuisines")
    @Expose
    var cuisines: List<String> = listOf()

    @SerializedName("address")
    @Expose
    var address: String = ""

    @SerializedName("city")
    @Expose
    var city: String = ""

    @SerializedName("zipcode")
    @Expose
    var zipcode: String? = null

    @SerializedName("state") //
    @Expose
    var state: String = ""

    @SerializedName("description")
    @Expose
    var description: String = ""

    @SerializedName("country")
    @Expose
    var country: String = ""

    @SerializedName("google_rating")
    @Expose
    var googleRating: String = ""

    @SerializedName("yelp_rating")
    @Expose
    var yelpRating: String = ""

    @SerializedName("amenities")
    @Expose
    var amenities: List<Amenity> = listOf()
}