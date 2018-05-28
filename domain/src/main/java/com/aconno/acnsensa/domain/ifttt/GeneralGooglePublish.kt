package com.aconno.acnsensa.domain.ifttt

import java.io.Serializable

class GeneralGooglePublish(
    override val id: Long,
    override val name: String,
    override val projectId: String,
    override val region: String,
    override val deviceRegistry: String,
    override val device: String,
    override val privateKey: String,
    override var enabled: Boolean
) : GooglePublish, Serializable