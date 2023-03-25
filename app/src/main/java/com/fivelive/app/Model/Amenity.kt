package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Amenity {
    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("image")
    @Expose
    var image: String = ""
}