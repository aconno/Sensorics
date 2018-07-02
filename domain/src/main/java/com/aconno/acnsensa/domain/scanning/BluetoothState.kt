package com.aconno.acnsensa.domain.scanning

data class BluetoothState(val state: Int) {
    companion object {
        const val BLUETOOTH_OFF = 0
        const val BLUETOOTH_ON = 1
    }
}