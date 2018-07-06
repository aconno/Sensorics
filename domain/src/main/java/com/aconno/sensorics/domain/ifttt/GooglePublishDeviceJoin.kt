package com.aconno.sensorics.domain.ifttt

interface GooglePublishDeviceJoin : PublishDeviceJoin {
    val gId: Long
    override val dId: String
}