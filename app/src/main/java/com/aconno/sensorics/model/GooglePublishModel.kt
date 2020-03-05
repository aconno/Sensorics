package com.aconno.sensorics.model

import android.os.Parcel
import android.os.Parcelable

class GooglePublishModel(
    id: Long,
    name: String,
    val projectId: String,
    val region: String,
    val deviceRegistry: String,
    val device: String,
    val privateKey: String,
    enabled: Boolean,
    timeType: String,
    timeMillis: Long,
    lastTimeMillis: Long,
    dataString: String
) : BasePublishModel(id, name, enabled, timeType, timeMillis, lastTimeMillis, dataString) {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(projectId)
        parcel.writeString(region)
        parcel.writeString(deviceRegistry)
        parcel.writeString(device)
        parcel.writeString(privateKey)
        parcel.writeByte(if (enabled) 1 else 0)
        parcel.writeString(timeType)
        parcel.writeLong(timeMillis)
        parcel.writeLong(lastTimeMillis)
        parcel.writeString(dataString)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GooglePublishModel> {
        override fun createFromParcel(parcel: Parcel): GooglePublishModel {
            return GooglePublishModel(parcel)
        }

        override fun newArray(size: Int): Array<GooglePublishModel?> {
            return arrayOfNulls(size)
        }
    }


}