package com.aconno.acnsensa.domain

import com.aconno.acnsensa.domain.model.readings.Reading

/**
 * @aconno
 */
interface FileStorage {
    fun storeReading(reading: Reading, fileName: String)
}