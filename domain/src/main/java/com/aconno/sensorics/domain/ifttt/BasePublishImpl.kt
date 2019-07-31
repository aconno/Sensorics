package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

class BasePublishImpl(
    override val id: Long,
    override val name: String,
    override var enabled: Boolean,
    override var timeType: String,
    override var timeMillis: Long,
    override var lastTimeMillis: Long,
    override var dataString: String,
    override val type: PublishType
) : BasePublish