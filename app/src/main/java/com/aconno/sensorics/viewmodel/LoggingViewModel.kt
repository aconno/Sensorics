package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.ui.logs.LoggingAdapter

/**
 * @author julio.mendoza on 3/4/19.
 */
class LoggingViewModel : ViewModel() {

    private val logItems = mutableListOf<String>()

    fun log(info: String) {
        log(info, LoggingAdapter.LoggingLevel.INFO)
    }

    fun logError(info: String) {
        log(info, LoggingAdapter.LoggingLevel.ERROR)
    }

    fun logWarning(info: String) {
        log(info, LoggingAdapter.LoggingLevel.WARNING)
    }

    private fun log(info: String, loggingLevel: LoggingAdapter.LoggingLevel) {
        
    }

    companion object {
        private const val LOG_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.SS"
        private const val LOG_FORMAT = "%s : %s"
    }

}