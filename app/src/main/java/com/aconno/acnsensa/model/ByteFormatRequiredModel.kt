package com.aconno.acnsensa.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ByteFormatRequiredModel
    (
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("index")
    @Expose
    var index: Int,
    @SerializedName("value")
    @Expose
    var value: String
)