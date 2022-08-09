package com.aconno.sensorics.domain

/**
 * This is disposable value class that holds data until first reading
 */
class DisposableValue<T>(value: T) {
    var value: T? = value
    private set
    get() {
        val tmp = field
        field = null
        return tmp
    }
}