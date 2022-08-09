package com.aconno.sensorics.data.repository.resources.format

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FormatJsonModel(
    @SerializedName("id")
    @Expose
    var id: String,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("icon")
    @Expose
    var icon: String,
    @SerializedName("format")
    @Expose
    var format: List<ByteFormatJsonModel>,
    @SerializedName("format_required")
    @Expose
    var formatRequired: List<ByteFormatRequiredJsonModel>,
    @SerializedName("connectable")
    @Expose
    var connectible: Boolean,
    @SerializedName("connection_write")
    @Expose
    var connectionWriteList: List<ConnectionWriteJsonModel>?,
    @SerializedName("connection_read")
    @Expose
    var connectionReadList: List<ConnectionReadJsonModel>?,
    @SerializedName("settings_support")
    @Expose
    var settingsSupportJsonModel: SettingsSupportJsonModel?
)