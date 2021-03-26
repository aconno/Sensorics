package com.aconno.sensorics

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.DeviceRelationModel
import com.google.android.material.snackbar.Snackbar
import java.util.*

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

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

/**
 * Need to make "when" exhaustive
 */
val <T> T.exhaustive: T
    get() = this

fun Activity.showToast(@StringRes msgRes: Int) = Toast.makeText(this, msgRes, Toast.LENGTH_LONG)
    .show()

fun Activity.showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun Fragment.showToast(msg: String) = activity?.showToast(msg)

fun Fragment.showToast(@StringRes msgRes: Int) = activity?.showToast(msgRes)

inline fun View.snack(
    @StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
): Snackbar {
    return snack(resources.getString(messageRes), length, f)
}

inline fun View.snack(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
): Snackbar {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
    return snack
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

fun String.lowercaseCapitalize(): String {
    return this.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault())
}