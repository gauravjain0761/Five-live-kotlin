package com.fivelive.app.Model.graph

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PeakHour {
    @SerializedName("peak_start")
    @Expose
    var peakStart = 0

    @SerializedName("peak_max")
    @Expose
    var peakMax = 0

    @SerializedName("peak_end")
    @Expose
    var peakEnd = 0

    @SerializedName("peak_intensity")
    @Expose
    var peakIntensity = 0

    @SerializedName("peak_delta_mean_week")
    @Expose
    var peakDeltaMeanWeek = 0
}