package com.aconno.acnsensa.domain

import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult

object TestUtils {

    const val VECTOR_ADVERTISEMENT = 1
    const val SCALAR_ADVERTISEMENT = 2
    const val INVALID_ADVERTISEMENT = 3

    fun getTestScanResult(
        deviceName: String,
        deviceMacAddress: String,
        advertisementType: Int
    ): ScanResult {
        return ScanResult(
            getTestDevice(deviceName, deviceMacAddress),
            getTestAdvertisement(advertisementType)
        )
    }

    private fun getTestDevice(name: String, macAddress: String): Device {
        return Device(name, macAddress)
    }

    private fun getTestAdvertisement(type: Int): Advertisement {
        return when (type) {
            VECTOR_ADVERTISEMENT -> Advertisement(vectorsAdvertisement)
            SCALAR_ADVERTISEMENT -> Advertisement(scalarsAdvertisement)
            else -> Advertisement(invalidAdvertisement)
        }
    }

    private val vectorsAdvertisement: List<Byte> =
        listOf(
            0x02, 0x01, 0x04, 0x1A, 0xFF,
            0x59, 0x00, 0x17, 0xCF, 0x00,
            0x2C, 0x06, 0xA8, 0x0E, 0x4D,
            0x08, 0x4A, 0xFD, 0x61, 0x06,
            0xC0, 0xB5, 0x92, 0x0D, 0x50,
            0xFE, 0x33, 0x09, 0x00, 0x00
        ).map { it.toByte() }

    private val scalarsAdvertisement: List<Byte> =
        listOf(
            0x02, 0x01, 0x04, 0x1A, 0xFF,
            0x59, 0x00, 0x17, 0xCF, 0x01,
            0xB9, 0x6D, 0xE3, 0x41, 0x28,
            0x54, 0x45, 0x42, 0xB2, 0x4E,
            0x80, 0x44, 0x82, 0x0C, 0x48,
            0x40, 0x2B, 0x09, 0x00, 0x00
        ).map { it.toByte() }

    private val invalidAdvertisement: List<Byte> =
        listOf(0xFF, 0x01).map { it.toByte() }
}
