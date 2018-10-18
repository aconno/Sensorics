package com.aconno.sensorics.device.format.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ConnectionReadJsonModel(
    @SerializedName("serviceUUID")
    @Expose
    val serviceUUID: String,
    @SerializedName("characteristicUUID")
    @Expose
    val characteristicUUID: String,
    @SerializedName("characteristicName")
    @Expose
    val characteristicName: String
)