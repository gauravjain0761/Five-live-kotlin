package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FaqList {
    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("question")
    @Expose
    var question: String = ""

    @SerializedName("answer")
    @Expose
    var answer: String = ""
}