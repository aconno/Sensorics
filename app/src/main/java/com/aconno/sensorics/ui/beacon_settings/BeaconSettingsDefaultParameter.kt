package com.aconno.sensorics.ui.beacon_settings


import com.aconno.sensorics.device.beacon.Parameter
import com.aconno.sensorics.domain.migrate.ValueConverterBase
import com.aconno.sensorics.domain.migrate.ValueConverters
import java.io.Serializable

class BeaconSettingsDefaultParameter(val name: String, val type: String) : Serializable {

    var writable: Boolean = false
    var id = -1
    var value: Any? = null
    var valueInternal: Any? = null
    val min: Int = -1
    val max: Int = 1

    class Builder {
        fun buildFromParameter(
            parameter: Parameter<Any>
        ): BeaconSettingsDefaultParameter {
            val defaultParameter = BeaconSettingsDefaultParameter(parameter.name, getLocalType(parameter.type))

            defaultParameter.writable = parameter.writable
            defaultParameter.id = parameter.id
            defaultParameter.valueInternal = parameter.getValue()
            return defaultParameter
        }

        private fun getLocalType(type: ValueConverterBase<Any>): String {
            return when (type) {
                ValueConverters.BOOLEAN -> "TYPE_PARAMETER_BOOLEAN"
                ValueConverters.UTF8_STRING -> "TYPE_PARAMETER_TEXT"
                ValueConverters.BYTE, ValueConverters.INT8, ValueConverters.UINT8, ValueConverters.INT16, ValueConverters.UINT16, ValueConverters.INT32, ValueConverters.UINT32 -> "TYPE_PARAMETER_NUMBER"
                ValueConverters.FLOAT -> "TYPE_PARAMETER_NUMBER_DECIMAL"
                ValueConverters.ENUM -> "TYPE_PARAMETER_ENUM"
                ValueConverters.TIME -> TODO()
                ValueConverters.MAC_ADDRESS -> TODO()
                else -> "TYPE_PARAMETER_TEXT"
            }
        }
    }

}