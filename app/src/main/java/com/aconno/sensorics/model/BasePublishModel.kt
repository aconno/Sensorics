package com.aconno.sensorics.model

import android.os.Parcelable

abstract class BasePublishModel(
    val id: Long,
    val name: String, var enabled: Boolean,
    var timeType: String, var timeMillis: Long, var lastTimeMillis: Long,
    var dataString: String
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BasePublishModel

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}