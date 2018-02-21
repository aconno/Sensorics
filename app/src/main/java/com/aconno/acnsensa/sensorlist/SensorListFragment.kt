package com.aconno.acnsensa.sensorlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import kotlinx.android.synthetic.main.fragment_sensor_list.*
import kotlinx.android.synthetic.main.item_sensor.view.*

class SensorListFragment : Fragment() {

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
}