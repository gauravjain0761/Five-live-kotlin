package com.fivelive.app.Model.graph

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VenueInfo {
    @SerializedName("venue_id")
    @Expose
    var venueId: String = ""

    @SerializedName("venue_name")
    @Expose
    var venueName: String = ""

    @SerializedName("venue_address")
    @Expose
    var venueAddress: String = ""

    @SerializedName("venue_timezone")
    @Expose
    var venueTimezone: String = ""
}