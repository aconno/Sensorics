package com.aconno.acnsensa.data.publisher

import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.interactor.filter.Reading
import com.aconno.acnsensa.domain.model.Device

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

    override fun publish(reading: Reading) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPublishable(device: Device): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}