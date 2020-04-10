package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

interface PublishDeviceJoin {
    val publishId: Long
    val deviceId: String
    val publishType: PublishType
}