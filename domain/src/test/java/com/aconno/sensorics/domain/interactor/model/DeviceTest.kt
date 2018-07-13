package com.aconno.sensorics.domain.interactor.model

import com.aconno.sensorics.domain.model.Device
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class DeviceTest {

    companion object {
        const val FAKE_NAME = "Name"
        const val FAKE_ALIAS = "Alias"
        const val FAKE_MACADDRESS = "MA:CA:DD:RE:SS"
        const val FAKE_ICON = "Icon"
    }

    lateinit var device: Device

    @Before
    fun setUp() {
        device = Device(
            FAKE_NAME,
            FAKE_ALIAS,
            FAKE_MACADDRESS,
            FAKE_ICON
        )
    }

    @Test
    fun testDeviceConstructorHappyCase() {
        val name = device.name
        val alias = device.alias
        val macAddress = device.macAddress
        val icon = device.icon

        assertThat(name).isEqualTo(FAKE_NAME)
        assertThat(alias).isEqualTo(FAKE_ALIAS)
        assertThat(macAddress).isEqualTo(FAKE_MACADDRESS)
        assertThat(icon).isEqualTo(FAKE_ICON)
    }
}