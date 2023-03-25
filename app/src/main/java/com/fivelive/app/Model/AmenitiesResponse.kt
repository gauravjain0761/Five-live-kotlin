package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AmenitiesResponse {
    @SerializedName("status")
    @Expose
    var status: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("business_amenities")
    @Expose
    var businessAmenities: BusinessAmenities? = null
}