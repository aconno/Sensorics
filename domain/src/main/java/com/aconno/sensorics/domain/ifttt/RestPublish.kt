package com.aconno.sensorics.domain.ifttt


/**
 * @author aconno
 */
interface RestPublish : BasePublish {
    val url: String
    val method: String
}
