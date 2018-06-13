package com.aconno.acnsensa.domain

import com.aconno.acnsensa.domain.interactor.filter.Reading

interface FileStorage {

    fun storeReading(reading: Reading, fileName: String)
}