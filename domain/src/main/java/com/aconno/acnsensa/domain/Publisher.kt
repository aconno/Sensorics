package com.aconno.acnsensa.domain

import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.model.readings.Reading

interface Publisher {

    fun publish(reading: SensorReading)

    fun isPublishable(device: Device): Boolean

    fun closeConnection()

    //Maybe Id
    fun getPublishData(): BasePublish

    fun test(testConnectionCallback: TestConnectionCallback)

    interface TestConnectionCallback {
        fun onSuccess()
        fun onFail()
    }
}