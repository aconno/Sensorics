package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.resources.format.*
import com.aconno.sensorics.domain.format.*
import com.udojava.evalex.Expression

class FormatJsonConverter {

    fun toAdvertisementFormat(formatJsonModel: FormatJsonModel): AdvertisementFormat {
        return GenericFormat(
            formatJsonModel.id,
            formatJsonModel.name,
            formatJsonModel.icon,
            formatJsonModel.format.map { toByteFormat(it) },
            formatJsonModel.formatRequired.map { toByteFormatRequired(it) },
            formatJsonModel.connectible,
            formatJsonModel.connectionWriteList?.map { toConnectionWrite(it) },
            formatJsonModel.connectionReadList?.map { toConnectionRead(it) },
            toSettingsSupport(formatJsonModel.settingsSupportJsonModel)
        )
    }

    private fun toSettingsSupport(settingsSupportJsonModel: SettingsSupportJsonModel?): SettingsSupport? {
        settingsSupportJsonModel?.let {
            return SettingsSupport(
                settingsSupportJsonModel.index,
                settingsSupportJsonModel.mask.replace("0x", "", true).toInt(16).toByte()
            )
        }
        return null
    }

    private fun toByteFormat(byteFormatJsonModel: ByteFormatJsonModel): ByteFormat {
        return ByteFormat(
            byteFormatJsonModel.name,
            byteFormatJsonModel.startIndexInclusive,
            byteFormatJsonModel.endIndexExclusive,
            byteFormatJsonModel.reversed,
            byteFormatJsonModel.dataType,
            getFormulaExpression(byteFormatJsonModel.formula)
        )
    }

    private fun getFormulaExpression(formula: String?): Expression? {
        if (formula != null && !formula.isBlank()) {
            return Expression(formula)
        }

        return null
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