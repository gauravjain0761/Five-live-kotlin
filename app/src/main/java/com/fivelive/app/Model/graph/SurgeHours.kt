package com.fivelive.app.Model.graph

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SurgeHours(
    @SerializedName("most_people_come")
    @Expose
    var mostPeopleCome: String,
    @SerializedName("most_people_leave")
    @Expose
    var mostPeopleLeave: String
)