package com.aconno.acnsensa.data.converter

import com.aconno.acnsensa.domain.model.Reading
import java.text.SimpleDateFormat
import java.util.*

object PublisherDataConverter {

    val date = Date()
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    init {
        timeFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    fun convert(reading: Reading): List<String> {
        return listOf(getJsonString(reading.type.toString(), reading.timestamp, reading.value))
    }

    private fun getJsonString(sensorType: String, timestamp: Long, value: Number): String {
        date.time = timestamp
        return "{\n" +
                "  \"type\": \"$sensorType\",\n" +
                "  \"timestamp\": \"${timeFormat.format(date)}\",\n" +
                "  \"value\": $value\n" +
                "}"
    }
}