package com.aconno.sensorics.domain.ifttt

class GeneralRestHeader(
    override val id: Long,
    override val rId: Long,
    override val key: String,
    override val value: String
) : RestHeader