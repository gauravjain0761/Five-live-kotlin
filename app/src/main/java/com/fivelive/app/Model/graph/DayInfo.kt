package com.fivelive.app.Model.graph

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DayInfo(
    @SerializedName("day_int")
    @Expose
    var dayInt: Int,
    @SerializedName("day_text")
    @Expose
    var dayText: String,

    @SerializedName("venue_open")
    @Expose
    var venueOpen: String,

    @SerializedName("venue_closed")
    @Expose
    var venueClosed: String,

    @SerializedName("day_rank_mean")
    @Expose
    var dayRankMean: Int,
    @SerializedName("day_rank_max")
    @Expose
    var dayRankMax: Int,
    @SerializedName("day_mean")
    @Expose
    var dayMean: Int,
    @SerializedName("day_max")
    @Expose
    var dayMax: Int
)