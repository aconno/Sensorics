package com.aconno.sensorics.domain.logs

data class Log(val info: String,
               val timestamp:Long,
               val loggingLevel: LoggingLevel,
               val deviceMacAddress: String): Comparable<Log> {

    override fun compareTo(other: Log): Int {
        return this.timestamp.compareTo(other.timestamp)
    }

}

enum class LoggingLevel {
    INFO, WARNING, ERROR
}