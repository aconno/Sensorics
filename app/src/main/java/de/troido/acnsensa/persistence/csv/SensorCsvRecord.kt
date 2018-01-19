package de.troido.acnsensa.persistence.csv

import de.troido.acnsensa.data.SensorCount
import de.troido.acnsensa.data.SensorCsv
import de.troido.acnsensa.data.SensorStats1
import de.troido.acnsensa.data.SensorStats2

sealed class SensorCsvRecord

data class SensorRawCsvRecord(val time: Int,
                              val battery: Int,
                              val light: Int,
                              val temperature: Double,
                              val accelerometerX: Double,
                              val accelerometerY: Double,
                              val accelerometerZ: Double,
                              val magnetometerX: Double,
                              val magnetometerY: Double,
                              val magnetometerZ: Double) : SensorCsvRecord() {

    constructor(data: SensorCsv) : this(
            data.time,
            data.battery.toInt(),
            data.sensors.light.value.toInt(),
            data.sensors.temperature.value.toDouble(),
            data.sensors.accelerometerX.value.toDouble(),
            data.sensors.accelerometerY.value.toDouble(),
            data.sensors.accelerometerZ.value.toDouble(),
            data.sensors.magnetometerX.value.toDouble(),
            data.sensors.magnetometerY.value.toDouble(),
            data.sensors.magnetometerZ.value.toDouble()
    )
}

data class SensorCounterCsvRecord(val time: Int,
                                  val battery: Int,
                                  val light: Int,
                                  val temperature: Int,
                                  val accelerometerX: Int,
                                  val accelerometerY: Int,
                                  val accelerometerZ: Int,
                                  val magnetometerX: Int,
                                  val magnetometerY: Int,
                                  val magnetometerZ: Int) : SensorCsvRecord() {

    constructor(data: SensorCount) : this(
            data.time, data.battery.toInt(), data.light, data.temperature,
            data.accelerometerX, data.accelerometerY, data.accelerometerZ,
            data.magnetometerX, data.magnetometerY, data.magnetometerZ
    )
}

data class SensorStatsCsvRecord(
        val time: Int, val battery: Int,
        val lightMin: Int, val lightMax: Int, val lightAvg: Int,
        val temperatureMin: Int, val temperatureMax: Int, val temperatureAvg: Int,
        val accelerometerXMin: Double, val accelerometerYMin: Double, val accelerometerZMin: Double,
        val accelerometerXMax: Double, val accelerometerYMax: Double, val accelerometerZMax: Double,
        val accelerometerXAvg: Double, val accelerometerYAvg: Double, val accelerometerZAvg: Double,
        val magnetometerXMin: Double, val magnetometerYMin: Double, val magnetometerZMin: Double,
        val magnetometerXMax: Double, val magnetometerYMax: Double, val magnetometerZMax: Double,
        val magnetometerXAvg: Double, val magnetometerYAvg: Double, val magnetometerZAvg: Double
) : SensorCsvRecord() {

    constructor(stats1: SensorStats1, stats2: SensorStats2) : this(
            stats1.time, stats1.battery.toInt(),
            stats2.light.min.toInt(), stats2.light.max.toInt(), stats2.light.avg.toInt(),
            stats2.temperature.min.toInt(), stats2.temperature.max.toInt(),
            stats2.temperature.avg.toInt(),
            stats1.accelerometer.min.x.toDouble(), stats1.accelerometer.min.y.toDouble(),
            stats1.accelerometer.min.z.toDouble(),
            stats1.accelerometer.max.x.toDouble(), stats1.accelerometer.max.y.toDouble(),
            stats1.accelerometer.max.z.toDouble(),
            stats1.accelerometer.avg.x.toDouble(), stats1.accelerometer.avg.y.toDouble(),
            stats1.accelerometer.avg.z.toDouble(),
            stats2.magnetometer.min.x.toDouble(), stats2.magnetometer.min.y.toDouble(),
            stats2.magnetometer.min.z.toDouble(),
            stats2.magnetometer.max.x.toDouble(), stats2.magnetometer.max.y.toDouble(),
            stats2.magnetometer.max.z.toDouble(),
            stats2.magnetometer.avg.x.toDouble(), stats2.magnetometer.avg.y.toDouble(),
            stats2.magnetometer.avg.z.toDouble()
    )
}
