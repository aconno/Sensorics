package com.aconno.sensorics.model.mapper

import android.support.annotation.ColorRes
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.logs.Log
import com.aconno.sensorics.domain.logs.LoggingLevel
import com.aconno.sensorics.model.LogModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LogModelMapper @Inject constructor() {

    fun transform(log: Log): LogModel {
        return LogModel(formatLogInfo(log.info, log.timestamp), getColorRes(log.loggingLevel))
    }

    @ColorRes
    private fun getColorRes(loggingLevel: LoggingLevel): Int {
        return when(loggingLevel) {
            LoggingLevel.INFO -> R.color.logging_info
            LoggingLevel.WARNING -> R.color.logging_warning
            LoggingLevel.ERROR -> R.color.logging_error
            else -> R.color.logging_error
        }
    }

    private fun formatLogInfo(info: String, timestamp: Long): String {
        val dateFormat = SimpleDateFormat(LOG_DATE_FORMAT, Locale.getDefault())
        val formattedTime = dateFormat.format(Date(timestamp))
        return String.format(LOG_FORMAT, formattedTime, info)
    }

    companion object {
        private const val LOG_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.SS"
        private const val LOG_FORMAT = "%s : %s"
    }
}