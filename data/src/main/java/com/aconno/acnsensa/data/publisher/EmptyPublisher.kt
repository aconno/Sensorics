package com.aconno.acnsensa.data.publisher

import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.model.readings.Reading

class EmptyPublisher : Publisher {

    override fun getPublishData(): BasePublish {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeConnection() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun test(testConnectionCallback: Publisher.TestConnectionCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun publish(reading: SensorReading) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPublishable(device: Device): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}