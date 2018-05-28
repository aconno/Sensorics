package com.aconno.acnsensa.data.http

import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.model.readings.Reading

class EmptyPublisher() : Publisher {
    override fun publish(reading: Reading) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeConnection() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}