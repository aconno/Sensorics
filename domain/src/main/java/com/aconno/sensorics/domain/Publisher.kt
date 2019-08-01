package com.aconno.sensorics.domain

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.model.Reading

interface Publisher {

    fun publish(readings: List<Reading>)

    fun closeConnection()

    //Maybe Id
    fun getPublishData(): BasePublish

    fun test(testConnectionCallback: TestConnectionCallback)

    interface TestConnectionCallback {
        fun onConnectionStart()
        fun onConnectionSuccess()
        fun onConnectionFail(exception: Throwable?)
    }
}