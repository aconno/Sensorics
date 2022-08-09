package com.aconno.sensorics.domain

interface AudioAlarm {
    fun start()
    fun stop()
    fun isRunning(): Boolean
}