package com.aconno.sensorics.data.repository.resources

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ConfigFileJsonModel(
    @SerializedName("device_screen_path")
    @Expose
    val deviceScreenPath: String,
    @SerializedName("format_path")
    @Expose
    val formatPath: String,
    @SerializedName("icon_path")
    @Expose
    val iconPath: String,
    @SerializedName("id")
    @Expose
    val id: String,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("usecase_screen_path")
    @Expose
    val usecaseScreenPath: String
)