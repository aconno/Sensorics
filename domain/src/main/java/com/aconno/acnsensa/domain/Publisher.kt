package com.aconno.acnsensa.domain

interface Publisher {

    fun publish(topic: String, message: String)
}