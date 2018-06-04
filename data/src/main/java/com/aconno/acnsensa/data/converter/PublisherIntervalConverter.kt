package com.aconno.acnsensa.data.converter

import java.util.concurrent.TimeUnit

object PublisherIntervalConverter {


    fun calculateMillis(timeCount: String, timeType: String): Long {
        return when (timeType) {
            "Secs" -> TimeUnit.SECONDS.toMillis(timeCount.toLong())
            "Mins" -> TimeUnit.MINUTES.toMillis(timeCount.toLong())
            "Hours" -> TimeUnit.HOURS.toMillis(timeCount.toLong())
            "Days" -> TimeUnit.DAYS.toMillis(timeCount.toLong())
            else -> throw IllegalArgumentException("Illegal Publish Time Type Provided.")
        }
    }

    fun calculateCountFromMillis(timeMillis: Long, timeType: String): String {
        return when (timeType) {
            "Secs" -> TimeUnit.MILLISECONDS.toSeconds(timeMillis).toString()
            "Mins" -> TimeUnit.MILLISECONDS.toMinutes(timeMillis).toString()
            "Hours" -> TimeUnit.MILLISECONDS.toHours(timeMillis).toString()
            "Days" -> TimeUnit.MILLISECONDS.toDays(timeMillis).toString()
            else -> throw IllegalArgumentException("Illegal Publish Time Type Provided.")
        }
    }

}