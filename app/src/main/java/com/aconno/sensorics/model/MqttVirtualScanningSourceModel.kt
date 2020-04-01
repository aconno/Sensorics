package com.aconno.sensorics.model

import android.os.Parcel
import android.os.Parcelable
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.MqttVirtualScanningSourceProtocol

class MqttVirtualScanningSourceModel(
        id: Long,
        name: String, enabled: Boolean,
        val protocol : MqttVirtualScanningSourceProtocol,
        val address : String,
        val port : Int,
        val path : String,
        val clientId : String,
        val username : String,
        val password : String,
        val qualityOfService : Int
) : BaseVirtualScanningSourceModel(id,name,enabled) {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
        parcel.readString() ?: ""!!,
            parcel.readByte() != 0.toByte(),
            MqttVirtualScanningSourceProtocol.valueOf(
                parcel.readString() ?: ""!!
            ),
        parcel.readString() ?: ""!!,
            parcel.readInt(),
        parcel.readString() ?: ""!!,
        parcel.readString() ?: ""!!,
        parcel.readString() ?: ""!!,
        parcel.readString() ?: ""!!,
            parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.apply {
            writeLong(id)
            writeString(name)
            writeByte(if (enabled) 1 else 0)
            writeString(protocol.name)
            writeString(address)
            writeInt(port)
            writeString(path)
            writeString(clientId)
            writeString(username)
            writeString(password)
            writeInt(qualityOfService)
        }
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