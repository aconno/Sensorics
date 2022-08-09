package com.aconno.sensorics.domain

interface AlarmServiceController {
    fun start()
    fun stop()

    companion object {
        var ACTION_ALARM_SNOOZE = "com.troido.intent.action.ACTION_ALARM_SNOOZE"
    }
}