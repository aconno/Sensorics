package com.aconno.sensorics.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ValueModel(
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