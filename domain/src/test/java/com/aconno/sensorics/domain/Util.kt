package com.aconno.sensorics.domain

import com.aconno.sensorics.domain.format.ByteFormat

class Util {

    companion object {

        val BEACON_BYTES = listOf(
            0x02.toByte(),//0
            0x01.toByte(),//1
            0x04.toByte(),//2
            0x1A.toByte(),//3
            0xFF.toByte(),//4
            0x59.toByte(),//5
            0x00.toByte(),//6
            0x17.toByte(),//7
            0xCF.toByte(),//8
            0x01.toByte(),//9
            0x00.toByte(),//10
            0x80.toByte(),//11
            0x15.toByte(),//12
            0x42.toByte(),//13
            0x10.toByte(),//14
            0x49.toByte(),//15
            0xEE.toByte(),//16
            0x41.toByte(),//17
            0xFE.toByte(),//18
            0xDA.toByte(),//19
            0x80.toByte(),//20
            0x44.toByte(),//21
            0x00.toByte(),//22
            0x90.toByte(),//23
            0xE2.toByte(),//24
            0x41.toByte(),//25
            0x64.toByte(),//26
            0xF5.toByte(),//27
            0x9E.toByte(),//28
            0x0F.toByte() //29
        ).toByteArray()

        val REQUIRED_FORMAT_BYTES = BEACON_BYTES.toList().subList(5, 29).toByteArray()

        val TEMPERATURE_FORMAT = ByteFormat(
            "Temperature",
            5,
            9,
            true,
            "FLOAT",
            null,
            0xFF.toByte()
        )

        val HUMIDITY_FORMAT = ByteFormat(
            "Humidity",
            9,
            13,
            true,
            "FLOAT",
            null,
            0xFF.toByte()
        )

        val PRESSURE_FORMAT = ByteFormat(
            "Pressure",
            13,
            17,
            true,
            "FLOAT",
            null,
            0xFF.toByte()
        )

        val LIGHT_FORMAT = ByteFormat(
            "Light",
            17,
            21,
            true,
            "FLOAT",
            null,
            0xFF.toByte()
        )

        val BATTERY_LEVEL_FORMAT = ByteFormat(
            "Battery Level",
            21,
            22,
            false,
            "BYTE",
            null,
            0xFF.toByte()
        )

        fun getListOfFormats(): HashMap<String, ByteFormat> {
            return hashMapOf(
                Pair("Temperature", TEMPERATURE_FORMAT),
                Pair("Humidity", HUMIDITY_FORMAT),
                Pair("Pressure", PRESSURE_FORMAT),
                Pair("Light", LIGHT_FORMAT),
                Pair("Battery Level", BATTERY_LEVEL_FORMAT)
            )
        }
    }
}