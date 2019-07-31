package com.aconno.sensorics.data.converter

import com.aconno.sensorics.domain.model.Reading
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class DataStringConverter(
    private var userDataString: String = ""
) {

    companion object {
        private const val VALUE = "value"
        private const val NAME = "name"
        private const val TS = "ts"
        private const val DATE = "date"
        private const val DEVICE = "device"
        private const val RSSI = "rssi"

        private const val NOT_VALID = -1
        private const val ONE_BY_ONE = 1
        private const val CHUNK = 2
    }

    private val mdyFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.GERMAN)
    private val list: MutableList<String> = mutableListOf()
    private val pattern = Pattern.compile("\\$\\s*(\\w+)")
    private var type: Int = 0

    init {
        mdyFormat.timeZone = TimeZone.getDefault()

        if (!userDataString.isEmpty()) {
            parseAndValidateDataString(userDataString)
        }
    }

    fun parseAndValidateDataString(everything: String): Boolean {
        val matcher = pattern.matcher(everything)
        while (matcher.find()) {
            list.add(matcher.group().replace("$", "").toLowerCase())
        }

        type = checkDataStringValid()
        println("Validation = ${type != NOT_VALID}")
        return type != NOT_VALID
    }

    private fun checkDataStringValid(): Int {
        val filteredList =
            list.filter { it != TS && it != DEVICE && it != DATE && it != RSSI} as MutableList<String>

        return if (filteredList.contains(VALUE) || filteredList.contains(NAME)) {
            filteredList.removeAll { it == VALUE || it == NAME }

            if (filteredList.size == 0) {
                ONE_BY_ONE
            } else {
                NOT_VALID
            }
        } else {
            CHUNK
        }
    }

    fun convert(readings: List<Reading>): List<String> {
        return if (type == ONE_BY_ONE) {
            getOneByOne(readings, userDataString)
        } else if (type == CHUNK) {
            listOf(getChunk(readings, userDataString))
        } else {
            listOf(userDataString)
        }.filter { !it.contains('$') }
    }

    fun convert(readings: List<Reading>, userDataString: String): List<String> {
        this@DataStringConverter.userDataString = userDataString
        if (parseAndValidateDataString(userDataString)) {
            return convert(readings)
        }

        return listOf(userDataString)
    }

    private fun getOneByOne(readings: List<Reading>, userDataString: String): List<String> {
        val responseList = mutableListOf<String>()

        readings.forEach { reading ->
            var newDataString = userDataString

            list.forEach {
                newDataString = when (it) {
                    NAME -> {
                        newDataString.replace("\$$it", reading.name)
                    }
                    VALUE -> {
                        newDataString.replace("\$$it", reading.value.toString())
                    }
                    TS -> {
                        newDataString.replace("\$$it", reading.timestamp.toString())
                    }
                    DATE -> {
                        newDataString.replace("\$$it", getTimestampWithTimeZone(reading.timestamp))
                    }
                    DEVICE -> {
                        newDataString.replace("\$$it", reading.device.macAddress)
                    }
                    RSSI -> {
                        newDataString.replace("\$$it", reading.rssi.toString())
                    }
                    else -> {
                        throw IllegalArgumentException()
                    }
                }
            }

            responseList.add(newDataString)
        }

        return responseList
    }

    fun convertDeviceAndTS(reading: Reading, userDataString: String): String {
        return userDataString.replace("\$$TS", reading.timestamp.toString())
            .replace("\$$DEVICE", reading.device.macAddress)
    }

    private fun getChunk(
        readings: List<Reading>,
        userDataString: String
    ): String {
        var newDataString = userDataString
        list.forEach {
            when (it) {
                TS -> {
                    newDataString = newDataString.replace("\$$TS", readings[0].timestamp.toString())
                }
                DATE -> {
                    newDataString = newDataString.replace(
                        "\$$DATE",
                        getTimestampWithTimeZone(readings[0].timestamp)
                    )
                }
                DEVICE -> {
                    newDataString = newDataString.replace(
                        "\$$DEVICE",
                        readings[0].device.macAddress
                    )
                }
                RSSI -> {
                    newDataString = newDataString.replace(
                        "\$$RSSI",
                        readings[0].rssi.toString()
                    )
                }
                else -> {
                    val find =
                        readings.find { reading ->
                            reading.name.replace(
                                " ",
                                "_"
                            ).toLowerCase() == it
                        }
                    if (find != null) {
                        newDataString = newDataString.replace("$$it", find.value.toString())
                    }
                }
            }

        }

        return newDataString
    }

    private fun getTimestampWithTimeZone(timeMillis: Long): String {
        //Prepare it for the GMT not for CET
        return mdyFormat.format(Date(timeMillis))
    }
}