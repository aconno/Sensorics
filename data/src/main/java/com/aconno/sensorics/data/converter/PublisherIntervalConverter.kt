package com.aconno.sensorics.data.converter

import java.util.concurrent.TimeUnit

object PublisherIntervalConverter {


    fun calculateMillis(timeCount: String, timeType: String): Long {
        return when (timeType) {
            "Sec" -> TimeUnit.SECONDS.toMillis(timeCount.toLong())
            "Min" -> TimeUnit.MINUTES.toMillis(timeCount.toLong())
            "Hour" -> TimeUnit.HOURS.toMillis(timeCount.toLong())
            "Days" -> TimeUnit.DAYS.toMillis(timeCount.toLong())
            else -> throw IllegalArgumentException("Illegal Publish Time Type Provided.")
        }
    }

    fun calculateCountFromMillis(timeMillis: Long, timeType: String): String {
        return when (timeType) {
            "Sec" -> TimeUnit.MILLISECONDS.toSeconds(timeMillis).toString()
            "Min" -> TimeUnit.MILLISECONDS.toMinutes(timeMillis).toString()
            "Hour" -> TimeUnit.MILLISECONDS.toHours(timeMillis).toString()
            "Days" -> TimeUnit.MILLISECONDS.toDays(timeMillis).toString()
            else -> throw IllegalArgumentException("Illegal Publish Time Type Provided.")
        }
    }

}