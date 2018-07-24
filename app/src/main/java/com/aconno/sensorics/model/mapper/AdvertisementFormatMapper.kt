package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.format.*
import com.aconno.sensorics.model.*

class AdvertisementFormatMapper {

    fun toAdvertisementFormat(genericFormatModel: GenericFormatModel): AdvertisementFormat {
        return GenericFormat(
            genericFormatModel.name,
            genericFormatModel.icon,
            genericFormatModel.format.map { toByteFormat(it) },
            genericFormatModel.formatRequired.map { toByteFormatRequired(it) },
            genericFormatModel.connectible,
            genericFormatModel.connectionWriteList?.map { toConnectionWrite(it) },
            genericFormatModel.connectionReadList?.map { toConnectionRead(it) }
        )
    }

    private fun toConnectionRead(connectionReadModel: ConnectionReadModel): ConnectionRead {
        return ConnectionRead(
            connectionReadModel.serviceUUID,
            connectionReadModel.characteristicUUID,
            connectionReadModel.characteristicName
        )
    }

    private fun toConnectionWrite(connectionWriteModel: ConnectionWriteModel): ConnectionWrite {
        return ConnectionWrite(
            connectionWriteModel.serviceUUID,
            connectionWriteModel.characteristicUUID,
            connectionWriteModel.characteristicName,
            connectionWriteModel.values.map { toValue(it) }
        )
    }

    private fun toValue(valueModel: ValueModel): Value {
        return Value(
            valueModel.name,
            valueModel.type,
            valueModel.value
        )
    }

    private fun toByteFormatRequired(byteFormatRequiredModel: ByteFormatRequiredModel): ByteFormatRequired {
        return ByteFormatRequired(
            byteFormatRequiredModel.name,
            byteFormatRequiredModel.index,
            (Integer.parseInt(
                byteFormatRequiredModel.value.replace("0x", ""),
                16
            ) and 0xff).toByte()
        )
    }

    private fun toByteFormat(byteFormatModel: ByteFormatModel): ByteFormat {
        return ByteFormat(
            byteFormatModel.name,
            byteFormatModel.startIndexInclusive,
            byteFormatModel.endIndexExclusive,
            byteFormatModel.reversed,
            byteFormatModel.dataType
        )
    }
}