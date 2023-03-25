package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RegularMenu {
    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null
}