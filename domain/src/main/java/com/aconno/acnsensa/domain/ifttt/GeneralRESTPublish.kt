package com.aconno.acnsensa.domain.ifttt

import java.io.Serializable

class GeneralRESTPublish(
    override val id: Long,
    override val name: String,
    override val url: String,
    override val method: String,
    override var enabled: Boolean
) : RESTPublish, Serializable