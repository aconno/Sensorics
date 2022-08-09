package com.aconno.sensorics.domain.time

interface TimeProvider {
    fun getLocalTimeOfDayInSeconds(): Int
}
