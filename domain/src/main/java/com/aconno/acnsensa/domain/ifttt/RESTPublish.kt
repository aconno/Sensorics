package com.aconno.acnsensa.domain.ifttt


/**
 * @author aconno
 */
interface RESTPublish : BasePublish {
    val url: String
    val method: String
}
