package com.aconno.sensorics.domain

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ByteOperationsTest {
    @Test
    fun execute_Test() {
        var result = ByteOperations.isolateAdvertisementTypes(
            byteArrayOf(0x05, 0xFF.toByte(), 0x01, 0x02, 0x03, 0x04)
        )
        assert(result.entries.toList().let {
            it[0].key == 0xFF.toByte() &&
                it[0].value.contentEquals(byteArrayOf(0x01, 0x02, 0x03, 0x04))
        })

        result = ByteOperations.isolateAdvertisementTypes(
            byteArrayOf(0x05, 0xFF.toByte(), 0x01, 0x02, 0x03, 0x04, 0x02, 0x00, 0x0F, 0x00, 0x00)
        )
        assert(result.entries.toList().let {
            it[0].key == 0xFF.toByte() &&
                it[0].value.contentEquals(byteArrayOf(0x01, 0x02, 0x03, 0x04)) &&
                it[1].key == 0x00.toByte() &&
                it[1].value.contentEquals(byteArrayOf(0x0F))
        })
    }
}