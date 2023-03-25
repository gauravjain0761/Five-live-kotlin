package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EditHHItem {
    @SerializedName("category")
    @Expose
    var category: List<Category> = listOf()
}