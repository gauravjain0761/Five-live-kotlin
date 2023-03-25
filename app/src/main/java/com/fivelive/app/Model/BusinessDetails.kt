package com.fivelive.app.Model

import com.fivelive.app.Model.graph.ForecastData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BusinessDetails {
    @SerializedName("images")
    @Expose
    var images: List<String> = listOf()

    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("website")
    @Expose
    var website: String? = null

    @SerializedName("call")
    @Expose
    var call: String? = null

    @SerializedName("cuisines")
    @Expose
    var cuisines: List<String> = listOf()

    @SerializedName("google_rating")
    @Expose
    var googleRating: String = ""

    @SerializedName("yelp_rating")
    @Expose
    var yelpRating: String = ""

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("zipcode")
    @Expose
    var zipcode: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("diatance")
    @Expose
    var diatance = 0

    @SerializedName("fav_status")
    @Expose
    var favStatus = 0

    @SerializedName("hh_hours_start_time")
    @Expose
    var hhHoursStartTime: String? = null

    @SerializedName("hh_hours_end_time")
    @Expose
    var hhHoursEndTime: String? = null

    @SerializedName("music_start_time")
    @Expose
    var musicStartTime: String? = null

    @SerializedName("music_end_time")
    @Expose
    var musicEndTime: String? = null

    @SerializedName("claim_status")
    @Expose
    var claimStatus = 0

    @SerializedName("happy_status")
    @Expose
    var happyStatus = 0

    @SerializedName("review_arr")
    @Expose
    var reviewArr: List<ReviewArray> = listOf()

    @SerializedName("hh_food")
    @Expose
    var hhFood: String? = null

    @SerializedName("hh_drink")
    @Expose
    var hhDrnk: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String = ""

    @SerializedName("longitude")
    @Expose
    var longitude: String = ""

    @SerializedName("happy_menue_status")
    @Expose
    var happy_menue_status = 0

    @SerializedName("reverse_menue_status")
    @Expose
    var reverse_menue_status = 0

    @SerializedName("brunch_menue_status")
    @Expose
    var brunch_menue_status = 0

    @SerializedName("happy_data")
    @Expose
    var happyData: List<HappyData> = listOf()

    @SerializedName("music_data")
    @Expose
    var musicData: List<MusicData> = listOf()

    @SerializedName("forecast_data")
    @Expose
    var forecastData: ForecastData? = null

    @SerializedName("avg_rating")
    @Expose
    var avgRating: String? = null

    @SerializedName("Facebook_link")
    @Expose
    var facebook_link: String? = ""

    @SerializedName("Twitter_link")
    @Expose
    var twitter_link: String? = ""

    @SerializedName("Instagram")
    @Expose
    var instagram: String? = ""
}