package com.aconno.acnsensa.domain.model

enum class SensorTypeSingle(val id: Int) {
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

    companion object {

        fun ofId(id: Int): SensorTypeSingle {
            return when (id) {
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
                else -> throw IllegalArgumentException("Invalid sensor type id: $id")
            }
        }
    }
}