package com.fivelive.app.Model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class ModelTest : Parcelable {
    var from: String? = null

    //public String description;
    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("business_id")
    @Expose
    var businessId = 0

    @SerializedName("days")
    @Expose
    var days: MutableList<String> = ArrayList()

    @SerializedName("hh_filter")
    @Expose
    var hhFilterList: ArrayList<String> = ArrayList()

    @SerializedName("drink")
    @Expose
    var drink: String? = null

    @SerializedName("food")
    @Expose
    var food: String? = null

    @SerializedName("start_time")
    @Expose
    var startTime: String? = null

    @SerializedName("end_time")
    @Expose
    var endTime: String? = null

    @SerializedName("status")
    @Expose
    var status = 0

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}