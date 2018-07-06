package com.aconno.sensorics.domain

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.Device

interface Publisher {

    fun publish(reading: Reading)

    fun isPublishable(device: Device): Boolean

    fun closeConnection()

    //Maybe Id
    fun getPublishData(): BasePublish

    fun test(testConnectionCallback: TestConnectionCallback)

    interface TestConnectionCallback {
        fun onConnectionStart()
        fun onConnectionSuccess()
        fun onConnectionFail()
    }
}