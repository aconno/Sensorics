package com.aconno.acnsensa.domain.ifttt

class GeneralGooglePublish(
    override val id: Long,
    override val name: String,
    override val projectId: String,
    override val region: String,
    override val deviceRegistry: String,
    override val device: String,
    override val privateKey: String,
    override var enabled: Boolean,
    override var timeType: String,
    override var timeMillis: Long,
    override var lastTimeMillis: Long
) : GooglePublish