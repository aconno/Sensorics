package com.aconno.sensorics.data.repository

interface PublishEntity {
    var id: Long
    var name: String
    var enabled: Boolean
    var timeType: String
    var timeMillis: Long
    var lastTimeMillis: Long
    var dataString: String
}