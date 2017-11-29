package de.troido.acnsensa.service

import de.troido.acnsensa.data.*
import de.troido.bleacon.data.*
import de.troido.ekstend.collections.component6
import de.troido.ekstend.collections.component7
import de.troido.ekstend.collections.component8
import de.troido.ekstend.collections.component9

private const val SENSOR_RAW1: Byte = 0x00
private const val SENSOR_RAW2: Byte = 0x01

private val raw1Deserializer = VecDeserializer(9, Int16Deserializer)
        .mapping { (gyroX, gyroY, gyroZ, accX, accY, accZ, magX, magY, magZ) ->
            SensorRaw1(
                    axisVectorFromShorts(::GyroscopeAxis, ::gyroscopeFromShort,
                            gyroX, gyroY, gyroZ),
                    axisVectorFromShorts(::AccelerometerAxis, ::accelerometerFromShort,
                            accX, accY, accZ),
                    axisVectorFromShorts(::MagnetometerAxis, ::magnetometerFromShort,
                            magX, magY, magZ)
            )
        }

private val raw2Deserializer = VecDeserializer(4, Float32Deserializer)
        .mapping { (temp, humidity, pressure, light) ->
            SensorRaw2(
                    Temperature(temp),
                    Humidity(humidity),
                    Pressure(pressure),
                    Light(light)
            )
        }

private fun <T> axisVectorFromShorts(ctor: (Float, Axis) -> T,
                                     transform: (Short) -> Float,
                                     x: Short, y: Short, z: Short): AxisVector<T>
        where T : AxisComponent,
              T : Sensor<*> =
        AxisVector(
                ctor(transform(x), Axis.X),
                ctor(transform(y), Axis.Y),
                ctor(transform(z), Axis.Z)
        )

val sensorDeserializer = SumDeserializer(deserializers = mapOf(
        SENSOR_RAW1 to raw1Deserializer,
        SENSOR_RAW2 to raw2Deserializer
))

/**
 * Returns floating point representation of the accelerometer axis value given as short.
 */
private fun accelerometerFromShort(value: Short): Float = value * 2.0f / 32768.0f

/**
 * Returns floating point representation of the magnetometer axis value given as short.
 */
private fun magnetometerFromShort(value: Short): Float = value * 0.00014f

/**
 * Returns floating point representation of the gyroscope axis value given as short.
 */
private fun gyroscopeFromShort(value: Short): Float = value * 245.0f / 32768.0f
