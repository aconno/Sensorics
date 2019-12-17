package com.aconno.sensorics.model

import android.os.Parcel
import android.os.Parcelable

class MqttVirtualScanningSourceModel(
        id: Long,
        name: String, enabled: Boolean
) : BaseVirtualScanningSourceModel(id,name,enabled) {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()!!,
            parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeLong(id)
        parcel?.writeString(name)
        parcel?.writeByte(if (enabled) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MqttVirtualScanningSourceModel> {
        override fun createFromParcel(parcel: Parcel): MqttVirtualScanningSourceModel {
            return MqttVirtualScanningSourceModel(parcel)
        }

        override fun newArray(size: Int): Array<MqttVirtualScanningSourceModel?> {
            return arrayOfNulls(size)
        }
    }
}