package com.aconno.sensorics.device.format.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ValueJsonModel(
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("type")
    @Expose
    val type: String,
    @SerializedName("value")
    @Expose
    val value: String
)