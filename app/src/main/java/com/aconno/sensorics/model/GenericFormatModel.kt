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
    var formatRequired: List<ByteFormatRequiredModel>,
    @SerializedName("connectable")
    @Expose
    var connectible: Boolean,
    @SerializedName("connection_write")
    @Expose
    var connectionWriteList: List<ConnectionWriteModel>?,
    @SerializedName("connection_read")
    @Expose
    var connectionReadList: List<ConnectionReadModel>?
)