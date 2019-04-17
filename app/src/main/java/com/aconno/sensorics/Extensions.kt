package com.aconno.sensorics

import android.util.SparseArray
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.DeviceRelationModel

fun Device.getRealName(): String {
    return if (alias.isBlank()) name else alias
}

fun DeviceRelationModel.getRealName(): String {
    return if (alias.isBlank()) name else alias
}

fun String.toHexByte(): Byte {
    return (Integer.parseInt(
        this.replace("0x", ""),
        16
    ) and 0xff).toByte()
}

fun <E> SparseArray<E>.find(predicate: (E) -> Boolean): E? {
    for (i in 0 until this.size()) {
        val element = this.valueAt(i)
        if (predicate(element)) {
            return element
        }
    }

    return null
}

fun <E> SparseArray<E>.keyOf(element: E): Int? {
    for (i in 0 until this.size()) {
        if (element == this[i]) {
            return i
        }
    }
    return null

}