package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

interface GooglePublishDeviceJoin : PublishDeviceJoin {
    override val publishType: PublishType
        get() = PublishType.GOOGLE
}