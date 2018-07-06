package com.aconno.sensorics.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GenericFormatModel(
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("icon")
    @Expose
    var icon: String,
    @SerializedName("format")
    @Expose
    var format: List<ByteFormatModel>,
    @SerializedName("format_required")
    @Expose
    var formatRequired: List<ByteFormatRequiredModel>
)