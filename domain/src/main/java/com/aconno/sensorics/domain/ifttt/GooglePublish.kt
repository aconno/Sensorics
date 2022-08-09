package com.aconno.sensorics.domain.ifttt

/**
 * @author aconno
 */
interface GooglePublish : BasePublish {
    val projectId: String
    val region: String
    val deviceRegistry: String
    val device: String
    val privateKey: String
}