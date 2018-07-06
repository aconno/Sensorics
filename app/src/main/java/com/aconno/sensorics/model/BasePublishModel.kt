package com.aconno.sensorics.model

import android.os.Parcelable

abstract class BasePublishModel(
    val id: Long,
    val name: String, var enabled: Boolean,
    var timeType: String, var timeMillis: Long, var lastTimeMillis: Long,
    var dataString: String
) : Parcelable