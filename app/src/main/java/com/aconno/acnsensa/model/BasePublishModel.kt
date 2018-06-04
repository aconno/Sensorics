package com.aconno.acnsensa.model

import android.os.Parcelable

abstract class BasePublishModel(
    val id: Long,
    val name: String, var enabled: Boolean,
    var timeType: String, var timeMillis: Long, var lastTimeMillis: Long
) : Parcelable