package com.aconno.bluetooth

import java.util.*

object UUIDProvider {
    fun provideFullUUID(shortUUID: String): UUID {
        if (shortUUID.length != 4) {
            throw IllegalArgumentException("Short UUID should be 2 bytes (4 hex characters)")
        }
        return UUID.fromString("cc52$shortUUID-9adb-4c37-bc48-376f5fee8851")
    }
}