package com.aconno.sensorics.domain.ifttt

interface RestPublishDeviceJoin : PublishDeviceJoin {
    val rId: Long
    override val dId: String
}