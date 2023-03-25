package com.fivelive.app.Model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class CategoryData: Parcelable {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("size")
    @Expose
    var size: String? = null

    @SerializedName("regular_price")
    @Expose
    var regularPrice: String? = null

    @SerializedName("offer_price")
    @Expose
    var offerPrice: String? = null

    @SerializedName("percentage")
    @Expose
    var percentage: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("type")
    @Expose
    var type = 0

    @SerializedName("id")
    @Expose
    var itemId: String? = null

    @SerializedName("category")
    @Expose
    var itemCategory: String? = null
}