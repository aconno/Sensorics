package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.logs.LogEntity
import com.aconno.sensorics.domain.logs.Log
import com.aconno.sensorics.domain.logs.LoggingLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogDataMapper @Inject constructor(){

    /**
     * Transform a [LogEntity] into an [Log].
     *
     * @param logEntity Object to be transformed.
     * @return [Log]
     */
    fun transform(logEntity: LogEntity): Log {
        return Log(logEntity.info, logEntity.timestamp, getLoggingLevel(logEntity.loggingLevel),
                logEntity.deviceMacAddress)
    }

    private fun getLoggingLevel(code: Int): LoggingLevel {
        return when (code) {
            INFO_CODE -> LoggingLevel.INFO
            WARNING_CODE -> LoggingLevel.WARNING
            ERROR_CODE -> LoggingLevel.ERROR
            else -> LoggingLevel.ERROR
        }
    }

    companion object {
        private const val INFO_CODE = 0
        private const val WARNING_CODE = 1
        private const val ERROR_CODE = -1
    }
}