package com.aconno.sensorics.ui.configure

import com.aconno.bluetooth.beacon.Parameter
import com.aconno.bluetooth.beacon.ValueConverter
import java.io.Serializable

data class DefaultParameter(val name: String, val type: String): Serializable{
    var min: Int = -1
    var max: Int = -1
    var unit: String = ""
    var writable: Boolean = false
    var id = -1
    var value: Any? = null
    var choices: List<String>? = null

    class Builder{
        fun buildFromParameter(parameter: Parameter): DefaultParameter {
            val defaultParameter = DefaultParameter(parameter.name, getLocalType(parameter.type))
            defaultParameter.min = parameter.min
            defaultParameter.max = parameter.max
            defaultParameter.unit = parameter.unit
            defaultParameter.writable = parameter.writable
            defaultParameter.id = parameter.id
            defaultParameter.value = parameter.value
            defaultParameter.choices = parameter.choices
            return defaultParameter
        }

        private fun getLocalType(type: ValueConverter): String{
            return when (type) {
                ValueConverter.BOOLEAN -> "TYPE_PARAMETER_BOOLEAN"
                ValueConverter.UTF8STRING -> "TYPE_PARAMETER_TEXT"
                ValueConverter.BYTE, ValueConverter.SINT8, ValueConverter.UINT8, ValueConverter.SINT16, ValueConverter.UINT16, ValueConverter.SINT32, ValueConverter.UINT32 -> "TYPE_PARAMETER_NUMBER"
                ValueConverter.FLOAT -> "TYPE_PARAMETER_NUMBER_DECIMAL"
                ValueConverter.ENUM -> "TYPE_PARAMETER_ENUM"
                ValueConverter.TIME -> TODO()
                ValueConverter.MAC_ADDRESS -> TODO()
            }
        }

    }

}