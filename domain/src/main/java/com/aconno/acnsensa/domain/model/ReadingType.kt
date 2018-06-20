package com.aconno.acnsensa.domain.model

enum class ReadingType(val id: Int) {

    OTHER(0),
    TEMPERATURE(1),
    LIGHT(2),
    HUMIDITY(3),
    PRESSURE(4),
    MAGNETOMETER_X(5),
    MAGNETOMETER_Y(6),
    MAGNETOMETER_Z(7),
    ACCELEROMETER_X(8),
    ACCELEROMETER_Y(9),
    ACCELEROMETER_Z(10),
    GYROSCOPE_X(11),
    GYROSCOPE_Y(12),
    GYROSCOPE_Z(13),
    BATTERY_LEVEL(14);

    override fun toString(): String {
        return when (this) {
            OTHER -> "Other"
            TEMPERATURE -> "Temperature"
            LIGHT -> "Light"
            HUMIDITY -> "Humidity"
            PRESSURE -> "Pressure"
            MAGNETOMETER_X -> "Magnetometer X"
            MAGNETOMETER_Y -> "Magnetometer Y"
            MAGNETOMETER_Z -> "Magnetometer Z"
            ACCELEROMETER_X -> "Accelerometer X"
            ACCELEROMETER_Y -> "Accelerometer Y"
            ACCELEROMETER_Z -> "Accelerometer Z"
            GYROSCOPE_X -> "Gyroscope X"
            GYROSCOPE_Y -> "Gyroscope Y"
            GYROSCOPE_Z -> "Gyroscope Z"
            BATTERY_LEVEL -> "Battery Level"
        }
    }

    fun toDataString(): String {
        return when (this) {
            OTHER -> "Other"
            TEMPERATURE -> "Temperature"
            LIGHT -> "Light"
            HUMIDITY -> "Humidity"
            PRESSURE -> "Pressure"
            MAGNETOMETER_X -> "Magnetometer_X"
            MAGNETOMETER_Y -> "Magnetometer_Y"
            MAGNETOMETER_Z -> "Magnetometer_Z"
            ACCELEROMETER_X -> "Accelerometer_X"
            ACCELEROMETER_Y -> "Accelerometer_Y"
            ACCELEROMETER_Z -> "Accelerometer_Z"
            GYROSCOPE_X -> "Gyroscope_X"
            GYROSCOPE_Y -> "Gyroscope_Y"
            GYROSCOPE_Z -> "Gyroscope_Z"
            BATTERY_LEVEL -> "Battery_Level"
        }
    }

    companion object {

        fun fromId(id: Int): ReadingType {
            return when (id) {
                0 -> OTHER
                1 -> TEMPERATURE
                2 -> LIGHT
                3 -> HUMIDITY
                4 -> PRESSURE
                5 -> MAGNETOMETER_X
                6 -> MAGNETOMETER_Y
                7 -> MAGNETOMETER_Z
                8 -> ACCELEROMETER_X
                9 -> ACCELEROMETER_Y
                10 -> ACCELEROMETER_Z
                11 -> GYROSCOPE_X
                12 -> GYROSCOPE_Y
                13 -> GYROSCOPE_Z
                14 -> BATTERY_LEVEL
                else -> throw IllegalArgumentException("Invalid ReadingType id: $id")
            }
        }

        fun fromString(string: String): ReadingType {
            return when (string) {
                "Temperature" -> TEMPERATURE
                "Light" -> LIGHT
                "Humidity" -> HUMIDITY
                "Pressure" -> PRESSURE
                "Magnetometer X" -> MAGNETOMETER_X
                "Magnetometer Y" -> MAGNETOMETER_Y
                "Magnetometer Z" -> MAGNETOMETER_Z
                "Accelerometer X" -> ACCELEROMETER_X
                "Accelerometer Y" -> ACCELEROMETER_Y
                "Accelerometer Z" -> ACCELEROMETER_Z
                "Gyroscope X" -> GYROSCOPE_X
                "Gyroscope Y" -> GYROSCOPE_Y
                "Gyroscope Z" -> GYROSCOPE_Z
                "Battery Level" -> BATTERY_LEVEL
                else -> OTHER
            }
        }
    }
}