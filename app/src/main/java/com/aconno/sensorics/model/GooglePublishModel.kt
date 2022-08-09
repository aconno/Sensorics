package com.aconno.sensorics.model

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

class GooglePublishModel(
    id: Long,
    name: String,
    val projectId: String,
    val region: String,
    val deviceRegistry: String,
    val device: String,
    val privateKey: String,
    enabled: Boolean,
    timeType: String,
    timeMillis: Long,
    lastTimeMillis: Long,
    dataString: String
) : BasePublishModel(id, PublishType.GOOGLE, name, enabled, timeType, timeMillis, lastTimeMillis, dataString)