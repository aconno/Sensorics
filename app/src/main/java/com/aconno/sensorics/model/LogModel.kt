package com.aconno.sensorics.model

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorRes

class LogModel(val formattedInfo: String,
               @ColorRes val colorResId: Int): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(formattedInfo)
        parcel.writeInt(colorResId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LogModel> {
        override fun createFromParcel(parcel: Parcel): LogModel {
            return LogModel(parcel)
        }

        override fun newArray(size: Int): Array<LogModel?> {
            return arrayOfNulls(size)
        }
    }
}