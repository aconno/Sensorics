package com.aconno.sensorics.model

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

class RestPublishModel(
    id: Long,
    name: String,
    val url: String,
    val method: String,
    enabled: Boolean,
    timeType: String,
    timeMillis: Long,
    lastTimeMillis: Long,
    dataString: String
) : BasePublishModel(
    id,
    PublishType.REST,
    name,
    enabled,
    timeType,
    timeMillis,
    lastTimeMillis,
    dataString
)