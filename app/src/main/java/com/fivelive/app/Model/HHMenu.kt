package com.fivelive.app.Model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class HHMenu(
    @SerializedName("title")
    @Expose
    var title: String?,
    @SerializedName("image")
    @Expose
    var image: String
) : Parcelable