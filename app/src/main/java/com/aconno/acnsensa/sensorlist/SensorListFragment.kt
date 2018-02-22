package com.aconno.acnsensa.sensorlist

import android.arch.lifecycle.Observer
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.SensorListViewModel
import com.aconno.acnsensa.device.bluetooth.BluetoothImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothPermission
import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorValuesUseCase
import kotlinx.android.synthetic.main.fragment_sensor_list.*
import kotlinx.android.synthetic.main.item_sensor.view.*

class SensorListFragment : Fragment() {

    private lateinit var sensorListViewModel: SensorListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bluetoothPermission: BluetoothPermission = object : BluetoothPermission {
            override var isGranted: Boolean
                get() = true
                set(value) {}

            override fun request() {

            }
        }

        val bluetooth = BluetoothImpl(bluetoothAdapter, bluetoothPermission)

        val advertisementMatcher = AdvertisementMatcher()

        val filterAdvertisementsUseCase = FilterAdvertisementsUseCase(advertisementMatcher)
        val sensorValuesUseCase = GetSensorValuesUseCase(advertisementMatcher)

        sensorListViewModel =
                SensorListViewModel(bluetooth, filterAdvertisementsUseCase, sensorValuesUseCase)
    }

    override fun onResume() {
        super.onResume()
        sensorListViewModel.getResult().observe(this, Observer { displaySensorValues(it) })
        sensorListViewModel.subscribe()
        sensorListViewModel.startScanning()
    }

    override fun onPause() {
        super.onPause()
        sensorListViewModel.stopScanning()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sensor_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        sensor_temperature.name.text = "Temperature"
        sensor_temperature.value.text = "0°C"
        sensor_light.name.text = "Light"
        sensor_light.value.text = "0.00%"
        sensor_humidity.name.text = "Humidity"
        sensor_humidity.value.text = "0.00%"
        sensor_pressure.name.text = "Pressure"
        sensor_pressure.value.text = "0000.00hPa"
        sensor_magnetometer_x.name.text = "Magnetometer X"
        sensor_magnetometer_x.value.text = "0.00µT"
        sensor_magnetometer_y.name.text = "Magnetometer Y"
        sensor_magnetometer_y.value.text = "0.00µT"
        sensor_magnetometer_z.name.text = "Magnetometer Z"
        sensor_magnetometer_z.value.text = "0.00µT"
        sensor_accelerometer_x.name.text = "Accelerometer X"
        sensor_accelerometer_x.value.text = "0.00mg"
        sensor_accelerometer_y.name.text = "Accelerometer Y"
        sensor_accelerometer_y.value.text = "0.00mg"
        sensor_accelerometer_z.name.text = "Accelerometer Z"
        sensor_accelerometer_z.value.text = "0.00mg"
        sensor_gyroscope_x.name.text = "Gyroscope X"
        sensor_gyroscope_x.value.text = "0.00dps"
        sensor_gyroscope_y.name.text = "Gyroscope Y"
        sensor_gyroscope_y.value.text = "0.00dps"
        sensor_gyroscope_z.name.text = "Gyroscope Z"
        sensor_gyroscope_z.value.text = "0.00dps"
    }

    private fun displaySensorValues(values: Map<String, Number>?) {
        values?.let {
            sensor_temperature.name.text = "Temperature"
            sensor_light.name.text = "Light"
            sensor_humidity.name.text = "Humidity"
            sensor_pressure.name.text = "Pressure"
            sensor_magnetometer_x.name.text = "Magnetometer X"
            sensor_magnetometer_y.name.text = "Magnetometer Y"
            sensor_magnetometer_z.name.text = "Magnetometer Z"
            sensor_accelerometer_x.name.text = "Accelerometer X"
            sensor_accelerometer_y.name.text = "Accelerometer Y"
            sensor_accelerometer_z.name.text = "Accelerometer Z"
            sensor_gyroscope_x.name.text = "Gyroscope X"
            sensor_gyroscope_y.name.text = "Gyroscope Y"
            sensor_gyroscope_z.name.text = "Gyroscope Z"

            val temperature = values["Temperature"]
            val light = values["Light"]
            val humidity = values["Humidity"]
            val pressure = values["Pressure"]
            val magnetometerX = values["Magnetometer X"]
            val magnetometerY = values["Magnetometer Y"]
            val magnetometerZ = values["Magnetometer Z"]
            val accelerometerX = values["Accelerometer X"]
            val accelerometerY = values["Accelerometer Y"]
            val accelerometerZ = values["Accelerometer Z"]
            val gyroscopeX = values["Gyroscope X"]
            val gyroscopeY = values["Gyroscope Y"]
            val gyroscopeZ = values["Gyroscope Z"]

            temperature?.let {

                sensor_temperature.value.text = "$it°C"
            }

            light?.let {

                sensor_light.value.text = "$it%"
            }

            humidity?.let {

                sensor_humidity.value.text = "$it%"
            }

            pressure?.let {

                sensor_pressure.value.text = "${it}hPa"
            }

            magnetometerX?.let {

                sensor_magnetometer_x.value.text = "${it}µT"
            }
            magnetometerY?.let {


                sensor_magnetometer_y.value.text = "${it}µT"
            }

            magnetometerZ?.let {

                sensor_magnetometer_z.value.text = "${it}µT"
            }
            accelerometerX?.let {

                sensor_accelerometer_x.value.text = "${it}mg"
            }
            accelerometerY?.let {

                sensor_accelerometer_y.value.text = "${it}mg"
            }
            accelerometerZ?.let {

                sensor_accelerometer_z.value.text = "0.00mg"
            }
            gyroscopeX?.let {

                sensor_gyroscope_x.value.text = "0.00dps"
            }
            gyroscopeY?.let {

                sensor_gyroscope_y.value.text = "0.00dps"
            }
            gyroscopeZ?.let {

                sensor_gyroscope_z.value.text = "0.00dps"
            }
        }
    }

}