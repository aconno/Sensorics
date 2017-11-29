package de.troido.acnsensa.persistence.preferences

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.twofortyfouram.locale.api.Intent.ACTION_REQUEST_QUERY
import de.troido.acnsensa.SensorApplication
import de.troido.acnsensa.data.*
import de.troido.acnsensa.tasker.SensorTaskerActivity
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_CLASS_DATA_TYPE
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_STRING_SENSOR
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_VALUE_NEW
import de.troido.acnsensa.tasker.SensorTaskerActivity.Companion.BUNDLE_EXTRA_VALUE_OLD
import de.troido.acnsensa.tasker.TaskerPlugin
import de.troido.ekstend.android.persistence.getStringFloat

val Context.sharedPreferences: SharedPreferences
    get() = getSharedPreferences(SensorApplication.PREFERENCES, Context.MODE_PRIVATE)

val SharedPreferences.light: Light
    get() = getWrappedFloat(::Light)

val SharedPreferences.temperature: Temperature
    get() = getWrappedFloat(::Temperature)

val SharedPreferences.humidity: Humidity
    get() = getWrappedFloat(::Humidity)

val SharedPreferences.pressure: Pressure
    get() = getWrappedFloat(::Pressure)

fun SharedPreferences.accelerometer(): AxisVector<AccelerometerAxis> =
        getAxisVector(::AccelerometerAxis)

fun SharedPreferences.magnetometer(): AxisVector<MagnetometerAxis> =
        getAxisVector(::MagnetometerAxis)

fun SharedPreferences.gyroscope(): AxisVector<GyroscopeAxis> =
        getAxisVector(::GyroscopeAxis)

private inline fun <reified T> SharedPreferences.getWrappedFloat(ctor: (Float) -> T): T =
        ctor(getStringFloat(T::class.java.simpleName))

private inline fun <reified T> SharedPreferences.getAxisVector(ctor: (Float, Axis) -> T)
        : AxisVector<T>
        where T : AxisComponent,
              T : Sensor<*> =
        AxisVector(getAxis(Axis.X, ctor), getAxis(Axis.Y, ctor), getAxis(Axis.Z, ctor))

private inline fun <reified T> SharedPreferences.getAxis(axis: Axis, ctor: (Float, Axis) -> T): T
        where T : AxisComponent,
              T : Sensor<*> =
        ctor(getStringFloat(axisKey<T>(axis)), axis)

val INTENT_REQUEST_REQUERY: Intent = Intent(ACTION_REQUEST_QUERY).putExtra(
        com.twofortyfouram.locale.api.Intent.EXTRA_STRING_ACTIVITY_CLASS_NAME,
        SensorTaskerActivity::class.java.name
)

/**
 * Persists the given sensor data to the shared preferences.
 * This allows the app to load the last encountered data even if the beacon is not in the
 * proximity of the device.
 */
fun Context.persistToPreferences(data: SensorRaw) = sharedPreferences.edit().apply {
    for (sensor in data.asList()) {
        val taskerBundle = Bundle()

        val k = when (sensor) {
            is AxisComponent -> axisKey(sensor::class.java, sensor.axis)
            else -> sensor.javaClass.simpleName
        }

        taskerBundle.putSerializable(BUNDLE_EXTRA_STRING_SENSOR, k)
        taskerBundle.putString(BUNDLE_EXTRA_VALUE_NEW, sensor.value.toString())
        taskerBundle.putString(BUNDLE_EXTRA_VALUE_OLD, sharedPreferences.getString(k, "ERROR"))
        taskerBundle.putSerializable(BUNDLE_EXTRA_CLASS_DATA_TYPE, sensor.value?.javaClass)
//
        TaskerPlugin.Event.addPassThroughData(INTENT_REQUEST_REQUERY, taskerBundle)
        TaskerPlugin.Event.addPassThroughMessageID(INTENT_REQUEST_REQUERY)
        applicationContext.sendBroadcast(INTENT_REQUEST_REQUERY)


        /*
         * We must manually (de)serialize the data into strings as shared preferences start
         * to treat everything as strings once the app is reinstalled, causing app crashes
         * and requiring the user to manually clear the app data.
         */
        putString(k, sensor.value.toString())
    }
}.apply()
