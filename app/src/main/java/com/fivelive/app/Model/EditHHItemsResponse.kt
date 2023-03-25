package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EditHHItemsResponse(
    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("message")
    @Expose
    var message: String = "",

    @SerializedName("ItemList")
    @Expose
    var itemList: EditHHItem
)