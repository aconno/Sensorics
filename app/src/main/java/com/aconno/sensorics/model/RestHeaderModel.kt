package com.aconno.sensorics.model

import android.os.Parcel
import android.os.Parcelable

class RestHeaderModel(
    val id: Long,
    val rId: Long,
    val key: String,
    val value: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(rId)
        parcel.writeString(key)
        parcel.writeString(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RestHeaderModel> {
        override fun createFromParcel(parcel: Parcel): RestHeaderModel {
            return RestHeaderModel(parcel)
        }

        override fun newArray(size: Int): Array<RestHeaderModel?> {
            return arrayOfNulls(size)
        }
    }
}