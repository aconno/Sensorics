package com.aconno.sensorics.data.repository.resources.format

import com.google.gson.annotations.SerializedName

class SettingsSupportJsonModel(
    @SerializedName("index")
    val index: Int,
    @SerializedName("mask")
    val mask: String
)