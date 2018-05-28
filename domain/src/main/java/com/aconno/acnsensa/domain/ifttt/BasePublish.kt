package com.aconno.acnsensa.domain.ifttt

interface BasePublish {
    val id: Long
    val name: String
    var enabled: Boolean
}