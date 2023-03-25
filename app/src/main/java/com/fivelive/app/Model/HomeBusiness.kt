package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeBusiness {
    // Business Count
    var businessCount = 0

    @SerializedName("images")
    @Expose
    var images: List<String> = listOf()

    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("cuisines")
    @Expose
    var cuisines: List<String> = listOf()

    @SerializedName("google_rating")
    @Expose
    var googleRating: String = ""

    @SerializedName("yelp_rating")
    @Expose
    var yelpRating: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("diatance")
    @Expose
    var diatance: String? = null

    @SerializedName("fav_status")
    @Expose
    var favStatus = 0

    @SerializedName("hh_hours_start_time")
    @Expose
    var hhHoursStartTime: String = ""

    @SerializedName("hh_hours_end_time")
    @Expose
    var hhHoursEndTime: String = ""

    @SerializedName("music_start_time")
    @Expose
    var musicStartTime: String = ""

    @SerializedName("music_end_time")
    @Expose
    var musicEndTime: String = ""

    @SerializedName("latitude")
    @Expose
    var latitude: String = ""

    @SerializedName("longitude")
    @Expose
    var longitude: String = ""

    @SerializedName("hh_drink")
    @Expose
    var hh_drink: String = ""

    @SerializedName("hh_food")
    @Expose
    var hh_food: String = ""

    @SerializedName("music_description")
    @Expose
    var music_description: String? = null

    @SerializedName("current_happy_hours")
    @Expose
    var current_happy_hours: String = ""
}