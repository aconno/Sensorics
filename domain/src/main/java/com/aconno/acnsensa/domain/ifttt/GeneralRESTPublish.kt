package com.aconno.acnsensa.domain.ifttt

class GeneralRESTPublish(
    override val id: Long,
    override val name: String,
    override val url: String,
    override val method: String,
    override var enabled: Boolean,
    override var timeType: String,
    override var timeMillis: Long,
    override var lastTimeMillis: Long
) : RESTPublish