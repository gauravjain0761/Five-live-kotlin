package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MenuListingResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("regular_menue")
    @Expose
    var regularMenue: List<RegularMenu> = listOf()
}