package com.aconno.sensorics

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.DeviceRelationModel
import com.google.android.material.snackbar.Snackbar

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
fun Activity.showToast(@StringRes msgRes: Int) = Toast.makeText(this, msgRes, Toast.LENGTH_LONG)
    .show()

fun Activity.showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

inline fun View.snack(
    @StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
) {
    snack(resources.getString(messageRes), length, f)
}

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(@StringRes actionRes: Int, color: Int? = null, listener: (View) -> Unit) {
    action(view.resources.getString(actionRes), color, listener)
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}

fun ByteArray.print(): String {
    val stringBuilder = StringBuilder()
    this.forEach {
        stringBuilder.append(String.format("%02X", it))
    }

    return stringBuilder.toString()
}