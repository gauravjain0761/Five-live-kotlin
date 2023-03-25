package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Image {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("image")
    @Expose
    var image: String = ""
}