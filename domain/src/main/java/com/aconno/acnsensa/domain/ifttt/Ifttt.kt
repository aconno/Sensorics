package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.readings.*
import io.reactivex.Completable
import io.reactivex.Single

/**
 * @aconno
 */
interface Input {
    val value: Float
    val type: Int
    val timestamp: Long
}

class GeneralInput(
    override val value: Float,
    override val type: Int,
    override val timestamp: Long
) : Input

interface Condition {
    fun isSatisfied(input: Input): Boolean
}

class LimitCondition(private val sensorType: Int, private val limit: Float, private val type: Int) :
    Condition {

    override fun isSatisfied(input: Input): Boolean {
        println("Condition Type: $type Value: ${input.value} Limit: $limit")
        return if (input.type == sensorType) {
            when (type) {
                LOWER_LIMIT -> input.value <= limit
                UPPER_LIMIT -> input.value >= limit
                else -> false
            }
        } else {
            false
        }
    }

    companion object {
        const val LOWER_LIMIT = 0
        const val UPPER_LIMIT = 1
    }
}

interface Outcome {
    fun execute()
}

class NotificationOutcome(private val message: String) : Outcome {
    override fun execute() {
        println("------------------------------------------------------")
        println(message)
        println("------------------------------------------------------")
    }
}

interface Action {
    val name: String
    fun processInput(input: Input)
}

class GeneralAction(
    override val name: String,
    private val condition: Condition,
    private val outcome: Outcome
) : Action {

    override fun processInput(input: Input) {
        if (condition.isSatisfied(input)) {
            outcome.execute()
        }
    }
}

class ReadingToInputUseCase() : SingleUseCaseWithParameter<List<Input>, List<Reading>> {
    override fun execute(parameter: List<Reading>): Single<List<Input>> {
        val inputs = parameter.map { getInputs(it) }.flatten()
        return Single.just(inputs)
    }

    private fun getInputs(reading: Reading): List<Input> {
        return when (reading) {
            is TemperatureReading -> listOf(getTemperatureInput(reading))
            is LightReading -> listOf(getLightInput(reading))
            is HumidityReading -> listOf(getHumidityInput(reading))
            is PressureReading -> listOf(getPressureInput(reading))
            is MagnetometerReading -> getMagnetometerInputs(reading)
            is AccelerometerReading -> getAccelerometerInputs(reading)
            is GyroscopeReading -> getGyroscopeInputs(reading)
            is BatteryReading -> listOf(getBatteryInput(reading))
            else -> throw IllegalArgumentException("Got invalid reading for input.")
        }
    }

    private fun getTemperatureInput(reading: TemperatureReading): Input {
        return GeneralInput(reading.temperature.toFloat(), INPUT_TEMPERATURE, reading.timestamp)
    }

    private fun getLightInput(reading: LightReading): Input {
        println("Light value is ${reading.light}")
        return GeneralInput(reading.light.toFloat(), INPUT_LIGHT, reading.timestamp)
    }

    private fun getHumidityInput(reading: HumidityReading): Input {
        return GeneralInput(reading.humidity.toFloat(), INPUT_HUMIDITY, reading.timestamp)
    }

    private fun getPressureInput(reading: PressureReading): Input {
        return GeneralInput(reading.pressure.toFloat(), INPUT_PRESSURE, reading.timestamp)
    }

    private fun getMagnetometerInputs(reading: MagnetometerReading): List<Input> {
        val x =
            GeneralInput(reading.magnetometerX.toFloat(), INPUT_MAGNETOMETER_X, reading.timestamp)
        val y =
            GeneralInput(reading.magnetometerY.toFloat(), INPUT_MAGNETOMETER_Y, reading.timestamp)
        val z =
            GeneralInput(reading.magnetometerZ.toFloat(), INPUT_MAGNETOMETER_Z, reading.timestamp)

        return listOf(x, y, z)
    }

    private fun getAccelerometerInputs(reading: AccelerometerReading): List<Input> {
        val x =
            GeneralInput(reading.accelerometerX.toFloat(), INPUT_ACCELEROMETER_X, reading.timestamp)
        val y =
            GeneralInput(reading.accelerometerY.toFloat(), INPUT_ACCELEROMETER_Y, reading.timestamp)
        val z =
            GeneralInput(reading.accelerometerZ.toFloat(), INPUT_ACCELEROMETER_Z, reading.timestamp)

        return listOf(x, y, z)
    }

    private fun getGyroscopeInputs(reading: GyroscopeReading): List<Input> {
        val x = GeneralInput(reading.gyroscopeX.toFloat(), INPUT_GYROSCOPE_X, reading.timestamp)
        val y = GeneralInput(reading.gyroscopeY.toFloat(), INPUT_GYROSCOPE_Y, reading.timestamp)
        val z = GeneralInput(reading.gyroscopeZ.toFloat(), INPUT_GYROSCOPE_Z, reading.timestamp)

        return listOf(x, y, z)
    }

    private fun getBatteryInput(reading: BatteryReading): Input {
        return GeneralInput(reading.batteryLevel.toFloat(), INPUT_BATTERY_LEVEL, reading.timestamp)
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

class HandleInputUseCase(
    private val actionsRespository: ActionsRespository
) : CompletableUseCaseWithParameter<Input> {
    override fun execute(parameter: Input): Completable {
        actionsRespository.getAllActions()
            .subscribe { actions -> actions.forEach { it.processInput(parameter) } }
        return Completable.complete()
    }
}

class AddActionUseCase(
    private val actionsRespository: ActionsRespository
) :
    CompletableUseCaseWithParameter<Action> {
    override fun execute(parameter: Action): Completable {
        actionsRespository.addAction(parameter)
        return Completable.complete()
    }
}

class GetAllActionsUseCase(
    private val actionsRespository: ActionsRespository
) : SingleUseCase<List<Action>> {
    override fun execute(): Single<List<Action>> {
        return actionsRespository.getAllActions()
    }
}

class DeleteActionUseCase(
    private val actionsRespository: ActionsRespository
) : CompletableUseCaseWithParameter<Action> {
    override fun execute(parameter: Action): Completable {
        actionsRespository.deleteAction(parameter)
        return Completable.complete()
    }
}

interface ActionsRespository {
    fun addAction(action: Action)
    fun deleteAction(action: Action)
    fun getAllActions(): Single<List<Action>>
}


