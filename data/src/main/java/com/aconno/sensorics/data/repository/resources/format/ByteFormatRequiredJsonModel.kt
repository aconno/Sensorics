package com.aconno.sensorics.data.repository.resources.format

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ByteFormatRequiredJsonModel(
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("index")
    @Expose
    var index: Int,
    @SerializedName("value")
    @Expose
    var value: String,
    @SerializedName("source")
    @Expose
    var source: Byte?
)