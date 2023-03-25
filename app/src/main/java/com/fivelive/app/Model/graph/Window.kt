package com.fivelive.app.Model.graph

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Window {
    @SerializedName("time_window_start")
    @Expose
    var timeWindowStart = 0

    @SerializedName("time_window_start_12h")
    @Expose
    var timeWindowStart12h: String = ""

    @SerializedName("day_window_start_int")
    @Expose
    var dayWindowStartInt = 0

    @SerializedName("day_window_start_txt")
    @Expose
    var dayWindowStartTxt: String = ""

    @SerializedName("day_window_end_int")
    @Expose
    var dayWindowEndInt = 0

    @SerializedName("day_window_end_txt")
    @Expose
    var dayWindowEndTxt: String = ""

    @SerializedName("time_window_end")
    @Expose
    var timeWindowEnd = 0

    @SerializedName("time_window_end_12h")
    @Expose
    var timeWindowEnd12h: String = ""

    @SerializedName("week_window")
    @Expose
    var weekWindow: String = ""
}