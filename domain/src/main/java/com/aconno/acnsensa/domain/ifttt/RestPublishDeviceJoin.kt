package com.aconno.acnsensa.domain.ifttt

interface RestPublishDeviceJoin : PublishDeviceJoin {
    val rId: Long
    override val dId: String
}