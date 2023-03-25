package com.fivelive.app.Model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class AddressList constructor(
    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("title")
    @Expose
    var title: String,

    @SerializedName("address")
    @Expose
    var address: String,

    @SerializedName("latitude")
    @Expose
    var latitude: String,

    @SerializedName("longitude")
    @Expose
    var longitude: String
) : Parcelable {


}