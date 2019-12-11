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

    fun toAdvertisementFormatJsonModel(advertisementFormat: AdvertisementFormat) : FormatJsonModel {
        val byteFormatsJsonModels = advertisementFormat.getFormat().values.map { toByteFormatJsonModel(it) }.toList()
        val formatRequiredJsonModels = advertisementFormat.getRequiredFormat().map { toByteFormatRequiredJsonModel(it) }

        return FormatJsonModel(
                advertisementFormat.id,
                advertisementFormat.getName(),
                advertisementFormat.getIcon(),
                byteFormatsJsonModels,
                formatRequiredJsonModels,
                advertisementFormat.isConnectible(),
                advertisementFormat.getConnectionWriteList()?.map { toConnectionWriteJsonModel(it) },
                advertisementFormat.getConnectionReadList()?.map { toConnectionReadJsonModel(it) },
                toSettingsSupportJsonModel(advertisementFormat.getSettingsSupport())
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

    private fun toSettingsSupportJsonModel(settingsSupport : SettingsSupport?) : SettingsSupportJsonModel?{
        settingsSupport?.let {
            return SettingsSupportJsonModel(
                    it.index,
                    "0x"+it.mask.toString(16)
            )
        }

        return null
    }

    private fun toByteFormatJsonModel(byteFormat: ByteFormat) : ByteFormatJsonModel {
        return ByteFormatJsonModel(
                byteFormat.name,
                byteFormat.startIndexInclusive,
                byteFormat.endIndexExclusive,
                byteFormat.isReversed,
                byteFormat.dataType,
                byteFormat.formula?.originalExpression,
                byteFormat.source
        )
    }

    private fun toByteFormat(byteFormatJsonModel: ByteFormatJsonModel): ByteFormat {
        return ByteFormat(
            byteFormatJsonModel.name,
            byteFormatJsonModel.startIndexInclusive,
            byteFormatJsonModel.endIndexExclusive,
            byteFormatJsonModel.reversed,
            byteFormatJsonModel.dataType,
            getFormulaExpression(byteFormatJsonModel.formula),
            byteFormatJsonModel.source ?: 0xFF.toByte()
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
            byteFormatRequiredJsonModel.value.replace(
                "0x", "", true
            ).toInt(16).toByte(),
            byteFormatRequiredJsonModel.source ?: 0xFF.toByte()
        )
    }

    private fun toByteFormatRequiredJsonModel(byteFormatRequired: ByteFormatRequired) : ByteFormatRequiredJsonModel {
        return ByteFormatRequiredJsonModel(
                byteFormatRequired.name,
                byteFormatRequired.position,
                "0x"+byteFormatRequired.value.toString(16),
                byteFormatRequired.source
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

    private fun toConnectionWriteJsonModel(connectionWrite: ConnectionWrite) : ConnectionWriteJsonModel {
        return ConnectionWriteJsonModel(
                connectionWrite.serviceUUID,
                connectionWrite.characteristicUUID,
                connectionWrite.characteristicName,
                connectionWrite.values.map { toValueJsonModel(it) }
        )
    }

    private fun toConnectionRead(connectionReadJsonModel: ConnectionReadJsonModel): ConnectionRead {
        return ConnectionRead(
            connectionReadJsonModel.serviceUUID,
            connectionReadJsonModel.characteristicUUID,
            connectionReadJsonModel.characteristicName
        )
    }

    private fun toConnectionReadJsonModel(connectionRead: ConnectionRead) : ConnectionReadJsonModel {
        return ConnectionReadJsonModel(
                connectionRead.serviceUUID,
                connectionRead.characteristicUUID,
                connectionRead.characteristicName
        )
    }

    private fun toValue(valueJsonModel: ValueJsonModel): Value {
        return Value(
            valueJsonModel.name,
            valueJsonModel.type,
            valueJsonModel.value
        )
    }

    private fun toValueJsonModel(value : Value) : ValueJsonModel {
        return ValueJsonModel(value.name,value.type,value.value)
    }
}