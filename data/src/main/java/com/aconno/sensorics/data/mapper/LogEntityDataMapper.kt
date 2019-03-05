package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.logs.LogEntity
import com.aconno.sensorics.domain.logs.Log
import com.aconno.sensorics.domain.logs.LoggingLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogEntityDataMapper @Inject constructor(){

    /**
     * Transform a [Log] into an [LogEntity].
     *
     * @param log Object to be transformed.
     * @return [LogEntity]
     */
    fun transform(log: Log): LogEntity {
        return LogEntity(0, log.info, log.timestamp,
                getLoggingLevelCode(log.loggingLevel), log.deviceMacAddress)
    }

    private fun getLoggingLevelCode(loggingLevel: LoggingLevel): Int {
        return when(loggingLevel) {
            LoggingLevel.INFO -> INFO_CODE
            LoggingLevel.WARNING -> WARNING_CODE
            LoggingLevel.ERROR -> ERROR_CODE
            else -> ERROR_CODE
        }
    }

    companion object {
        const val INFO_CODE = 0
        const val WARNING_CODE = 1
        const val ERROR_CODE = -1
    }
}