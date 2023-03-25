package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SavedAddressResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("address_list")
    @Expose
    var addressList: MutableList<AddressList> = mutableListOf()

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("requestKey")
    @Expose
    var requestKey: String? = null
}