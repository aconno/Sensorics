package com.aconno.sensorics.device.format

import com.aconno.sensorics.device.format.model.*
import com.aconno.sensorics.domain.format.*
import com.google.gson.Gson

class FormatJsonConverterImpl(
    private val gson: Gson
) : AdvertisementFormatJsonConverter {

    override fun toAdvertisementFormat(jsonString: String): AdvertisementFormat {
        val formatJsonModel = gson.fromJson(jsonString, FormatJsonModel::class.java)
        return GenericFormat(
            formatJsonModel.id,
            formatJsonModel.name,
            formatJsonModel.icon,
            formatJsonModel.format.map { toByteFormat(it) },
            formatJsonModel.formatRequired.map { toByteFormatRequired(it) },
            formatJsonModel.connectible,
            formatJsonModel.connectionWriteList?.map { toConnectionWrite(it) },
            formatJsonModel.connectionReadList?.map { toConnectionRead(it) }
        )
    }

    private fun toByteFormat(byteFormatJsonModel: ByteFormatJsonModel): ByteFormat {
        return ByteFormat(
            byteFormatJsonModel.name,
            byteFormatJsonModel.startIndexInclusive,
            byteFormatJsonModel.endIndexExclusive,
            byteFormatJsonModel.reversed,
            byteFormatJsonModel.dataType,
            byteFormatJsonModel.formula
        )
    }

    private fun toByteFormatRequired(
        byteFormatRequiredJsonModel: ByteFormatRequiredJsonModel
    ): ByteFormatRequired {
        return ByteFormatRequired(
            byteFormatRequiredJsonModel.name,
            byteFormatRequiredJsonModel.index,
            byteFormatRequiredJsonModel.value.replace("0x", "", true).toInt(16).toByte()
        )
    }

    private fun toConnectionWrite(
        connectionWriteJsonModel: ConnectionWriteJsonModel
    ): ConnectionWrite {
        return ConnectionWrite(
            connectionWriteJsonModel.serviceUUID,
            connectionWriteJsonModel.characteristicUUID,
            connectionWriteJsonModel.characteristicName,
            connectionWriteJsonModel.values.map { toValue(it) }
        )
    }

    private fun toConnectionRead(connectionReadJsonModel: ConnectionReadJsonModel): ConnectionRead {
        return ConnectionRead(
            connectionReadJsonModel.serviceUUID,
            connectionReadJsonModel.characteristicUUID,
            connectionReadJsonModel.characteristicName
        )
    }

    private fun toValue(valueJsonModel: ValueJsonModel): Value {
        return Value(
            valueJsonModel.name,
            valueJsonModel.type,
            valueJsonModel.value
        )
    }
}