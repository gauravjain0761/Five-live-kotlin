package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EditBusinessResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("edit_business_details")
    @Expose
    var editBusinessDetails: EditBusinessDetails? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("requestKey")
    @Expose
    var requestKey: String? = null
}