package com.aconno.acnsensa.domain

import com.aconno.acnsensa.domain.model.readings.Reading

interface Publisher {

    fun publish(reading: Reading)
}