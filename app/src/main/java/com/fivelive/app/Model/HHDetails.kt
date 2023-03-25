package com.fivelive.app.Model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class HHDetails constructor(
    @SerializedName("days")
    @Expose
    var days: String?,

    @SerializedName("food")
    @Expose
    var food: String?,

    @SerializedName("drink")
    @Expose
    var drink: String?,

    @SerializedName("start_time")
    @Expose
    var startTime: String?,

    @SerializedName("end_time")
    @Expose
    var endTime: String?
) : Parcelable