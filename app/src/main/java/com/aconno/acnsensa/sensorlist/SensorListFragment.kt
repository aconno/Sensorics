package com.aconno.acnsensa.sensorlist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.SensorListViewModel
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorValuesUseCase
import kotlinx.android.synthetic.main.fragment_sensor_list.*
import kotlinx.android.synthetic.main.view_sensor_card.view.*

//TODO: This needs refactoring.
class SensorListFragment : Fragment() {

    private lateinit var sensorListViewModel: SensorListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val advertisementMatcher = AdvertisementMatcher()

        val filterAdvertisementsUseCase = FilterAdvertisementsUseCase(advertisementMatcher)
        val sensorValuesUseCase = GetSensorValuesUseCase(advertisementMatcher)

        val acnSensaApplication: AcnSensaApplication? =
            activity?.application as? AcnSensaApplication
        acnSensaApplication?.let {
            val sensorListViewModelFactory = SensorListViewModelFactory(
                it.bluetooth,
                filterAdvertisementsUseCase,
                sensorValuesUseCase
            )
            sensorListViewModel = ViewModelProviders.of(this, sensorListViewModelFactory)
                .get(SensorListViewModel::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorListViewModel.getResult().observe(this, Observer { displaySensorValues(it) })
    }

    override fun onPause() {
        super.onPause()
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
        sensor_temperature.icon.setImageResource(R.drawable.ic_temperature)
        sensor_temperature.name.text = "Temperature"
        sensor_temperature.value.text = "0°C"

        sensor_light.icon.setImageResource(R.drawable.ic_light)
        sensor_light.name.text = "Light"
        sensor_light.value.text = "0.00%"

        sensor_humidity.icon.setImageResource(R.drawable.ic_humidity)
        sensor_humidity.name.text = "Humidity"
        sensor_humidity.value.text = "0.00%"

        sensor_pressure.icon.setImageResource(R.drawable.ic_pressure)
        sensor_pressure.name.text = "Pressure"
        sensor_pressure.value.text = "0000.00hPa"

        sensor_magnetometer_x.icon.setImageResource(R.drawable.ic_compass)
        sensor_magnetometer_x.name.text = "Magnetometer X"
        sensor_magnetometer_x.value.text = "0.00µT"

        sensor_magnetometer_y.icon.setImageResource(R.drawable.ic_compass)
        sensor_magnetometer_y.name.text = "Magnetometer Y"
        sensor_magnetometer_y.value.text = "0.00µT"

        sensor_magnetometer_z.icon.setImageResource(R.drawable.ic_compass)
        sensor_magnetometer_z.name.text = "Magnetometer Z"
        sensor_magnetometer_z.value.text = "0.00µT"

        sensor_accelerometer_x.icon.setImageResource(R.drawable.ic_acc)
        sensor_accelerometer_x.name.text = "Accelerometer X"
        sensor_accelerometer_x.value.text = "0.00mg"

        sensor_accelerometer_y.icon.setImageResource(R.drawable.ic_acc)
        sensor_accelerometer_y.name.text = "Accelerometer Y"
        sensor_accelerometer_y.value.text = "0.00mg"

        sensor_accelerometer_z.icon.setImageResource(R.drawable.ic_acc)
        sensor_accelerometer_z.name.text = "Accelerometer Z"
        sensor_accelerometer_z.value.text = "0.00mg"

        sensor_gyroscope_x.icon.setImageResource(R.drawable.ic_gyro)
        sensor_gyroscope_x.name.text = "Gyroscope X"
        sensor_gyroscope_x.value.text = "0.00dps"

        sensor_gyroscope_y.icon.setImageResource(R.drawable.ic_gyro)
        sensor_gyroscope_y.name.text = "Gyroscope Y"
        sensor_gyroscope_y.value.text = "0.00dps"

        sensor_gyroscope_z.icon.setImageResource(R.drawable.ic_gyro)
        sensor_gyroscope_z.name.text = "Gyroscope Z"
        sensor_gyroscope_z.value.text = "0.00dps"

    }

    private fun displaySensorValues(values: Map<String, Number>?) {
        Log.e("Display values!!!!!!", values.toString())
        values?.let {
            val temperatureLabel = "Temperature"
            val lightLabel = "Light"
            val humidityLabel = "Humidity"
            val pressureLabel = "Pressure"
            val magnetoXLabel = "Magnetometer X"
            val magnetoYLabel = "Magnetometer Y"
            val magnetoZLabel = "Magnetometer Z"
            val accelerometerXLabel = "Accelerometer X"
            val accelerometerYLabel = "Accelerometer Y"
            val accelerometerZLabel = "Accelerometer Z"
            val gyroXLabel = "Gyroscope X"
            val gyroYLabel = "Gyroscope Y"
            val gyroZLabel = "Gyroscope Z"

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

            temperature?.let { sensor_temperature.update(temperatureLabel, "$temperature°C") }
            light?.let { sensor_light.update(lightLabel, "$light%") }
            humidity?.let { sensor_humidity.update(humidityLabel, "$humidity%") }
            pressure?.let { sensor_pressure.update(pressureLabel, "${pressure}hPa") }

            magnetometerX?.let { sensor_magnetometer_x.update(magnetoXLabel, "${magnetometerX}µT") }
            magnetometerY?.let { sensor_magnetometer_y.update(magnetoYLabel, "${magnetometerY}µT") }
            magnetometerZ?.let { sensor_magnetometer_z.update(magnetoZLabel, "${magnetometerZ}µT") }

            accelerometerX
                ?.let { sensor_accelerometer_x.update(accelerometerXLabel, "${accelerometerX}mg") }
            accelerometerY
                ?.let { sensor_accelerometer_y.update(accelerometerYLabel, "${accelerometerY}mg") }
            accelerometerZ
                ?.let { sensor_accelerometer_z.update(accelerometerZLabel, "${accelerometerZ}mg") }

            gyroscopeX?.let { sensor_gyroscope_x.update(gyroXLabel, "${gyroscopeX}dps") }
            gyroscopeY?.let { sensor_gyroscope_y.update(gyroYLabel, "${gyroscopeY}dps") }
            gyroscopeZ?.let { sensor_gyroscope_z.update(gyroZLabel, "${gyroscopeZ}dps") }
        }
    }
}

class SensorListViewModelFactory(
    val bluetooth: Bluetooth,
    val filterAdvertisementsUseCase: FilterAdvertisementsUseCase,
    val sensorValuesUseCase: GetSensorValuesUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel: T? =
            SensorListViewModel(bluetooth, filterAdvertisementsUseCase, sensorValuesUseCase) as? T
        viewModel?.let { return viewModel }

        throw IllegalArgumentException("Invalid cast")
    }
}