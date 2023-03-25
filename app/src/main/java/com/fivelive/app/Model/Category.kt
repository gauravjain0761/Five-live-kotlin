package com.fivelive.app.Model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Category protected constructor(
    @SerializedName("category_name")
    @Expose
    var categoryName: String?,

    @SerializedName("category_data")
    @Expose
    var categoryData: List<CategoryData> = listOf()
) : Parcelable {

}