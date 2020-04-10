package com.aconno.sensorics.model

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

abstract class BasePublishModel(
    val id: Long,
    val type: PublishType,
    val name: String, var enabled: Boolean,
    var timeType: String, var timeMillis: Long, var lastTimeMillis: Long,
    var dataString: String
)