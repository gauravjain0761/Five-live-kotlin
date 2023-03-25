package com.fivelive.app.Model.graph

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HourAnalysis (
    @SerializedName("hour")
    @Expose
    var hour: Int = 0,

    @SerializedName("intensity_txt")
    @Expose
    var intensityTxt: String,

    @SerializedName("intensity_nr")
    @Expose
    var intensityNr: String
)