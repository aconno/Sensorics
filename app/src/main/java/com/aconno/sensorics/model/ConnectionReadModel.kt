package com.aconno.sensorics.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ConnectionReadModel(
    @SerializedName("serviceUUID")
    @Expose
    val serviceUUID: String,
    @SerializedName("characteristicUUID")
    @Expose
    val characteristicUUID: String
)