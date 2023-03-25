package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EditBusinessDetails {
    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("category")
    @Expose
    var category: MutableList<String> = mutableListOf()

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

    @SerializedName("zipcode")
    @Expose
    var zipcode: String? = null

    @SerializedName("website")
    @Expose
    var website: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("images")
    @Expose
    var images: MutableList<Image> = mutableListOf()

    @SerializedName("cuisines")
    @Expose
    var cuisines: List<Cuisine> = listOf()
}