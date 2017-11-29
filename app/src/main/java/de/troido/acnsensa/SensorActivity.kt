package de.troido.acnsensa

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.troido.acnsensa.data.*
import de.troido.acnsensa.persistence.preferences.*
import de.troido.acnsensa.service.SensorBinder
import de.troido.acnsensa.service.SensorBleService
import de.troido.ekstend.android.services.bindService
import de.troido.ekstend.android.services.startService
import de.troido.ekstend.android.services.subscriptionConnection
import de.troido.ekstend.android.system.permissionGate
import de.troido.ekstend.android.system.permissionGateResult
import kotlinx.android.synthetic.main.activity_main.*

private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WAKE_LOCK,
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
)

class SensorActivity : AppCompatActivity() {

    /**
     * Updates the UI on each data observation through the bound service.
     */
    private val conn = subscriptionConnection<SensorData, SensorBinder> {
        when (it) {
            is SensorRaw -> updateSensorUi(it.asList())
        }
    }

    override fun onResume() {
        super.onResume()
        updateFromPreferences()
        bindService<SensorBleService>(conn)
    }

    override fun onPause() {
        super.onPause()
        unbindService(conn)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Gates the rest of the application with the requested permissions.
        permissionGate(PERMISSIONS, this::startBle)
    }

    override fun onRequestPermissionsResult(requestCode: Int, PERMISSIONS: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, PERMISSIONS, grantResults)
        permissionGateResult(grantResults, R.string.permission_fail, this::startBle)
    }

    private fun startBle() {
        startService<SensorBleService>()
    }

    private fun updateFromPreferences() {
        sharedPreferences.apply {
            updateSensorUi(
                    listOf(light, temperature, humidity, pressure)
                            + accelerometer().asList()
                            + magnetometer().asList()
                            + gyroscope().asList()
            )
        }
    }

    private fun updateSensorUi(sensors: Iterable<Sensor<*>>) {
        for (sensor in sensors) when (sensor) {
            is Temperature -> sensor_temperature
            is Light -> sensor_light
            is Humidity -> sensor_humidity
            is Pressure -> sensor_pressure
            is AccelerometerAxis -> when (sensor.axis) {
                Axis.X -> sensor_accelerometer_x
                Axis.Y -> sensor_accelerometer_y
                Axis.Z -> sensor_accelerometer_z
            }
            is MagnetometerAxis -> when (sensor.axis) {
                Axis.X -> sensor_magnetometer_x
                Axis.Y -> sensor_magnetometer_y
                Axis.Z -> sensor_magnetometer_z
            }
            is GyroscopeAxis -> when (sensor.axis) {
                Axis.X -> sensor_gyroscope_x
                Axis.Y -> sensor_gyroscope_y
                Axis.Z -> sensor_gyroscope_z
            }
        }.update(sensor)
    }
}
