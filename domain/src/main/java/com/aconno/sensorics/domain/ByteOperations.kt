package com.aconno.sensorics.domain

object ByteOperations {

    fun isolateMsd(bytes: ByteArray): ByteArray {
        try {
            var i = 0
            while (i < bytes.size) {
                val length = bytes[i].toInt()
                val type = bytes[i + 1].toInt()

                if (type == -1) {
                    return bytes.copyOfRange(i + 2, i + length + 1)
                }

                if (i + length > bytes.size) {
                    return bytes
                }

                i += length
                i++
            }

            return bytes
        } catch (ex: Exception) {
            return bytes
        }
    }
}