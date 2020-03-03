package com.aconno.sensorics.domain

import java.io.Closeable

inline fun <T : Closeable?, R> T.tryUse(block: (T) -> R, catch: (Throwable) -> R): R {
    return try {
        this.use {
            block(this)
        }
    } catch (e: Throwable) {
        catch(e)
    }
}