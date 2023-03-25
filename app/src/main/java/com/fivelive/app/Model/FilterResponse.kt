package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FilterResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("filter_list")
    @Expose
    var filterList: List<FilterList> = listOf()

    @SerializedName("brunch_filter_list")
    @Expose
    var brunchFilterList: List<FilterList> = listOf()
}