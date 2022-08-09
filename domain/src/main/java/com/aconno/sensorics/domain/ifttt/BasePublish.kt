package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

interface BasePublish {
    val id: Long
    val name: String
    var enabled: Boolean
    var timeType: String
    var timeMillis: Long
    var lastTimeMillis: Long
    var dataString: String
    val type: PublishType
}