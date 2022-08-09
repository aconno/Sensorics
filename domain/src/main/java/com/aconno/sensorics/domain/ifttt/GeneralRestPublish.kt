package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

class GeneralRestPublish(
    override val id: Long,
    override val name: String,
    override val url: String,
    override val method: String,
    override var enabled: Boolean,
    override var timeType: String,
    override var timeMillis: Long,
    override var lastTimeMillis: Long,
    override var dataString: String
) : RestPublish {
    override val type: PublishType = PublishType.REST
}