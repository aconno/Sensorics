package com.aconno.acnsensa.domain

import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.model.Device

interface Publisher {

    fun publish(reading: Reading)

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