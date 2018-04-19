package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.Reading
import io.reactivex.Single

/**
 * @author aconno
 */
class ReadingToInputUseCase : SingleUseCaseWithParameter<List<Input>, List<Reading>> {
    override fun execute(parameter: List<Reading>): Single<List<Input>> {
        val inputs = parameter.map { getInputs(it) }.flatten()
        return Single.just(inputs)
    }

    private fun getInputs(reading: Reading): List<Input> {
        return when (reading.sensorType) {
            SensorType.TEMPERATURE -> getInput(reading, listOf(INPUT_TEMPERATURE))
            SensorType.LIGHT -> getInput(reading, listOf(INPUT_LIGHT))
            SensorType.HUMIDITY -> getInput(reading, listOf(INPUT_HUMIDITY))
            SensorType.PRESSURE -> getInput(reading, listOf(INPUT_PRESSURE))
            SensorType.MAGNETOMETER -> getInput(
                reading,
                listOf(INPUT_MAGNETOMETER_X, INPUT_MAGNETOMETER_Y, INPUT_MAGNETOMETER_Z)
            )
            SensorType.ACCELEROMETER -> getInput(
                reading,
                listOf(INPUT_ACCELEROMETER_X, INPUT_ACCELEROMETER_Y, INPUT_ACCELEROMETER_Z)
            )
            SensorType.GYROSCOPE -> getInput(
                reading,
                listOf(INPUT_GYROSCOPE_X, INPUT_GYROSCOPE_Y, INPUT_GYROSCOPE_Z)
            )
            SensorType.BATTERY_LEVEL -> getInput(reading, listOf(INPUT_BATTERY_LEVEL))
        }
    }

    private fun getInput(reading: Reading, inputTypes: List<Int>): List<Input> {
        return if (reading.values.size == inputTypes.size) {
            inputTypes.mapIndexed { index, inputType ->
                GeneralInput(reading.values[index].toFloat(), inputType, reading.timestamp)
            }
        } else {
            listOf()
        }
    }

    companion object {
        private const val INPUT_TEMPERATURE = 0
        private const val INPUT_LIGHT = 1
        private const val INPUT_HUMIDITY = 2
        private const val INPUT_PRESSURE = 3
        private const val INPUT_MAGNETOMETER_X = 4
        private const val INPUT_MAGNETOMETER_Y = 5
        private const val INPUT_MAGNETOMETER_Z = 6
        private const val INPUT_ACCELEROMETER_X = 7
        private const val INPUT_ACCELEROMETER_Y = 8
        private const val INPUT_ACCELEROMETER_Z = 9
        private const val INPUT_GYROSCOPE_X = 10
        private const val INPUT_GYROSCOPE_Y = 11
        private const val INPUT_GYROSCOPE_Z = 12
        private const val INPUT_BATTERY_LEVEL = 13
    }
}