package com.aconno.sensorics.data.repository.resources.format

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ConnectionWriteJsonModel(
    @SerializedName("serviceUUID")
    @Expose
    val serviceUUID: String,
    @SerializedName("characteristicUUID")
    @Expose
    val characteristicUUID: String,
    @SerializedName("characteristicName")
    @Expose
    val characteristicName: String,
    @SerializedName("values")
    @Expose
    val values: List<ValueJsonModel>
)