package com.aconno.sensorics

import android.content.Context
import java.util.concurrent.TimeUnit

object PublisherIntervalConverter {
    fun calculateMillis(context: Context, timeCount: String, timeType: String): Long {
        return when (timeType) {
            context.getString(R.string.publish_millis) -> timeCount.toLong()
            context.getString(R.string.publish_sec) -> TimeUnit.SECONDS.toMillis(timeCount.toLong())
            context.getString(R.string.publish_min) -> TimeUnit.MINUTES.toMillis(timeCount.toLong())
            context.getString(R.string.publish_hour) -> TimeUnit.HOURS.toMillis(timeCount.toLong())
            context.getString(R.string.publish_day) -> TimeUnit.DAYS.toMillis(timeCount.toLong())
            else -> throw IllegalArgumentException("Illegal Publish Time Type Provided.")
        }
    }

    fun calculateCountFromMillis(context: Context, timeMillis: Long, timeType: String): String {
        return when (timeType) {
            context.getString(R.string.publish_millis) -> timeMillis.toString()
            context.getString(R.string.publish_sec) -> TimeUnit.MILLISECONDS.toSeconds(timeMillis).toString()
            context.getString(R.string.publish_min) -> TimeUnit.MILLISECONDS.toMinutes(timeMillis).toString()
            context.getString(R.string.publish_hour) -> TimeUnit.MILLISECONDS.toHours(timeMillis).toString()
            context.getString(R.string.publish_day) -> TimeUnit.MILLISECONDS.toDays(timeMillis).toString()
            else -> throw IllegalArgumentException("Illegal Publish Time Type Provided.")
        }
    }
}