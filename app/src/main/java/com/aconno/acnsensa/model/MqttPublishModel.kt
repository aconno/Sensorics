package com.aconno.acnsensa.model

import android.os.Parcel
import android.os.Parcelable

class MqttPublishModel(
    id: Long,
    name: String,
    val url: String,
    val clientId: String,
    val username: String,
    val password: String,
    enabled: Boolean,
    timeType: String,
    timeMillis: Long,
    lastTimeMillis: Long
) : BasePublishModel(id, name, enabled, timeType, timeMillis, lastTimeMillis) {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeString(clientId)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeByte(if (enabled) 1 else 0)
        parcel.writeString(timeType)
        parcel.writeLong(timeMillis)
        parcel.writeLong(lastTimeMillis)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MqttPublishModel> {
        override fun createFromParcel(parcel: Parcel): MqttPublishModel {
            return MqttPublishModel(parcel)
        }

        override fun newArray(size: Int): Array<MqttPublishModel?> {
            return arrayOfNulls(size)
        }
    }
}