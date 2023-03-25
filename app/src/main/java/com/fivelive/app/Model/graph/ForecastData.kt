package com.fivelive.app.Model.graph

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForecastData(
    @SerializedName("status")
    @Expose
    var status: String,
    @SerializedName("epoch_analysis")
    @Expose
    var epochAnalysis: String,
    @SerializedName("venue_info")
    @Expose
    var venueInfo: VenueInfo,

    @SerializedName("analysis")
    @Expose
    var analysis: List<Analysis>?,

    @SerializedName("api_key_private")
    @Expose
    var apiKeyPrivate: String,
    @SerializedName("window")
    @Expose
    var window: Window,
)