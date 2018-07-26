package com.aconno.sensorics.domain.format

interface Connection {
    fun getName(): String
    fun isConnectible(): Boolean
    fun getConnectionWriteList(): List<ConnectionWrite>?
    fun getConnectionReadList(): List<ConnectionRead>?
}