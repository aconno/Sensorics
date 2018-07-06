package com.aconno.sensorics.domain

import com.aconno.sensorics.domain.model.Reading

interface FileStorage {

    fun storeReading(reading: Reading, fileName: String)
}