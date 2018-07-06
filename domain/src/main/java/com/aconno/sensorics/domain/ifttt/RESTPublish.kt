package com.aconno.sensorics.domain.ifttt


/**
 * @author aconno
 */
interface RESTPublish : BasePublish {
    val url: String
    val method: String
    val parameterName: String
}
