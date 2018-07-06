package com.aconno.sensorics.domain.model

/**
 * @author aconno
 */
data class ScanEvent(val type: Int, val message: String) {

    companion object {
        const val SCAN_START: Int = 0
        const val SCAN_STOP: Int = 1
        const val SCAN_FAILED_ALREADY_STARTED: Int = 2
        const val SCAN_FAILED: Int = 3
    }
}