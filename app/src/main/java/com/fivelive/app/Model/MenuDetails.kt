package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MenuDetails(
    @SerializedName("business_name")
    @Expose
    var businessName: String?,

    @SerializedName("cuisines")
    @Expose
    var cuisines: List<String>?,

    @SerializedName("address")
    @Expose
    var address: String?,

    @SerializedName("city")
    @Expose
    var city: String?,

    @SerializedName("state")
    @Expose
    var state: String?,

    @SerializedName("zipcode")
    @Expose
    var zipcode: String?,

    @SerializedName("country")
    @Expose
    var country: String?,

    @SerializedName("google_rating")
    @Expose
    var googleRating: String = "",

    @SerializedName("yelp_rating")
    @Expose
    var yelpRating: String = "",

    @SerializedName("hh_menue")
    @Expose
    var hhMenue: List<HHMenu>,

    @SerializedName("hh_details")
    @Expose
    var hhDetails: List<HHDetails> = listOf(),

    @SerializedName("reverse_items")
    @Expose
    var reverseHHItems: HHItems,

    @SerializedName("reverse_menue")
    @Expose
    var reverseMenu: List<HHMenu>,

    @SerializedName("reverse_details")
    @Expose
    var reverseHHDetails: List<HHDetails>,

    @SerializedName("hh_items")
    @Expose
    var hhItems: HHItems,

    @SerializedName("brunch_menue")
    @Expose
    var brunchMenu: List<HHMenu> = listOf(),

    @SerializedName("brunch_details")
    @Expose
    var brunchDetails: List<HHDetails> = listOf(),

    @SerializedName("basic_menue")
    @Expose
    var regularMenu: List<HHMenu> = listOf()
)