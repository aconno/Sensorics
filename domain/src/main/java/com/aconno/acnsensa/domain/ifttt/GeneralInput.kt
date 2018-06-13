package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.filter.ReadingType

class GeneralInput(
    override val macAddress: String,
    override val value: Float,
    override val type: ReadingType,
    override val timestamp: Long
) : Input