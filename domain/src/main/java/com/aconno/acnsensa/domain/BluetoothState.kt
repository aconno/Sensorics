package com.aconno.acnsensa.domain

data class BluetoothState(val state: Int) {
    companion object {
        const val BLUETOOTH_OFF = 0
        const val BLUETOOTH_ON = 1
    }
}