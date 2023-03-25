package com.fivelive.app.Model.graph

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Analysis(
    @SerializedName("day_info")
    @Expose
    var dayInfo: DayInfo,

    @SerializedName("busy_hours")
    @Expose
    var busyHours: List<Int>,

    @SerializedName("quiet_hours")
    @Expose
    var quietHours: List<Int>,

    @SerializedName("peak_hours")
    @Expose
    var peakHours: List<PeakHour>,

    @SerializedName("surge_hours")
    @Expose
    var surgeHours: SurgeHours,

    @SerializedName("hour_analysis")
    @Expose
    var hourAnalysis: List<HourAnalysis>,

    @SerializedName("day_raw")
    @Expose
    var dayRaw: List<Int>,
)