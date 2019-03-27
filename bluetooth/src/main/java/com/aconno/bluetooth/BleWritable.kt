package com.aconno.bluetooth

interface BleWritable {
    fun write(full: Boolean = true): List<Task>
}