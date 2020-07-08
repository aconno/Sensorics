package com.aconno.sensorics.domain.scanning


data class ScanEvent(val type: Int, val message: String) {

    companion object {

        const val SCAN_START: Int = 0
        const val SCAN_STOP: Int = 1
        const val SCAN_FAILED_ALREADY_STARTED: Int = 2
        const val SCAN_FAILED: Int = 3

        fun start() = ScanEvent(
            SCAN_START,
            "Scan start, timestamp: ${System.currentTimeMillis()}"
        )

        fun stop() = ScanEvent(
            SCAN_STOP,
            "Scan stop, timestamp: ${System.currentTimeMillis()}"
        )

        fun failedAlreadyStarted(errorCode: Int) = ScanEvent(
            SCAN_FAILED_ALREADY_STARTED,
            "Scan failed already started, error code: $errorCode"
        )

        fun failed(errorCode: Int) = ScanEvent(
            SCAN_FAILED,
            "Scan failed, error code: $errorCode"
        )

        fun failed(message: String?) = ScanEvent(SCAN_FAILED, message ?: "Scan failed")
    }
}