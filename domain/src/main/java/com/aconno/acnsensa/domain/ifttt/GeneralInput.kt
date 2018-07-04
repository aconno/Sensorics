package com.aconno.acnsensa.domain.ifttt

class GeneralInput(
    override val macAddress: String,
    override val value: Float,
    override val type: String,
    override val timestamp: Long
) : Input