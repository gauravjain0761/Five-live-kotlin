package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlanDetails {
    @SerializedName("package_name")
    @Expose
    var packageName: String? = null

    @SerializedName("end_date")
    @Expose
    var endDate: String? = null
}