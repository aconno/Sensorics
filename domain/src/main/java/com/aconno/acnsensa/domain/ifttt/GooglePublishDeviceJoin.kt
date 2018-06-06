package com.aconno.acnsensa.domain.ifttt

interface GooglePublishDeviceJoin : PublishDeviceJoin {
    val gId: Long
    override val dId: String
}