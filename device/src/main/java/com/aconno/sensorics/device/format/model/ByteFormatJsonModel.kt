package com.aconno.sensorics.device.format.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ByteFormatJsonModel(
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("start_index_inclusive")
    @Expose
    var startIndexInclusive: Int,
    @SerializedName("end_index_exclusive")
    @Expose
    var endIndexExclusive: Int,
    @SerializedName("reversed")
    @Expose
    var reversed: Boolean,
    @SerializedName("data_type")
    @Expose
    var dataType: String
)