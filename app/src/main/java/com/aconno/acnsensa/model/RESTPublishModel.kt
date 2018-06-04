package com.aconno.acnsensa.model

import android.os.Parcel
import android.os.Parcelable

class RESTPublishModel(
    id: Long,
    name: String,
    val url: String,
    val method: String,
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
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeString(method)
        parcel.writeByte(if (enabled) 1 else 0)
        parcel.writeString(timeType)
        parcel.writeLong(timeMillis)
        parcel.writeLong(lastTimeMillis)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RESTPublishModel> {
        override fun createFromParcel(parcel: Parcel): RESTPublishModel {
            return RESTPublishModel(parcel)
        }

        override fun newArray(size: Int): Array<RESTPublishModel?> {
            return arrayOfNulls(size)
        }
    }

}