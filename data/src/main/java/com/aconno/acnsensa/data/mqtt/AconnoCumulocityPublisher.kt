package com.aconno.acnsensa.data.mqtt

import android.content.Context
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.model.readings.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.nio.charset.Charset
import java.util.*

class AconnoCumulocityPublisher(context: Context, val username: String, val password: String) :
    Publisher {

    private val mqttAndroidClient: MqttAndroidClient

    private val messagesQueue: Queue<String> = LinkedList<String>()

    init {
        mqttAndroidClient = MqttAndroidClient(context, SERVER_URI, CLIENT_ID)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Timber.d("Connection complete to server: %s", serverURI)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Timber.d("Message arrived, topic: %s, message: %s", topic, message)
            }

            override fun connectionLost(cause: Throwable?) {
                Timber.d(cause, "Connection lost")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Timber.d("Delivery complete, token: %s", token)
            }
        })
    }

    private fun connect() {
        val options = getConnectOptions()
        try {
            mqttAndroidClient.connect(options, null, object : IMqttActionListener {

                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Timber.d("Successful connect to server, token: %s", asyncActionToken)
                    publishMessagesFromQueue()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Timber.e(exception, "Failed to connect to server")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun getConnectOptions(): MqttConnectOptions {
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true
        options.isCleanSession = false
        options.userName = username
        options.password = password.toCharArray()
        return options
    }

    private fun publish(message: String) {
        if (mqttAndroidClient.isConnected) {
            publishMessage(message)
        } else {
            connect()
            messagesQueue.add(message)
        }
    }

    private fun publishMessage(message: String) {
        mqttAndroidClient.publish(
            CUMULOSITY_SUBSCRIBTION_TOPIC,
            message.toByteArray(Charset.defaultCharset()),
            QUALITY_OF_SERVICE,
            RETENTION_POLICY
        )
    }

    private fun publishMessagesFromQueue() {
        while (messagesQueue.isNotEmpty()) {
            publish(messagesQueue.poll())
        }
    }

    override fun publish(reading: Reading) {
        val messages = mapReadingToMessage(reading)
        for (message in messages) {
            publish(message)
        }
    }

    private fun mapReadingToMessage(reading: Reading): List<String> {
        return when (reading) {
            is TemperatureReading -> generateTemperatureMessages(reading)
            is LightReading -> generateLightMessages(reading)
            is HumidityReading -> generateHumidityReading(reading)
            is PressureReading -> generatePressureReading(reading)
            is MagnetometerReading -> generateMagnetometerMessages(reading)
            is AccelerometerReading -> generateAccelerometerMessages(reading)
            is GyroscopeReading -> generateGyroscopeMessages(reading)
            is BatteryReading -> generateBatteryMesssage(reading)

            else -> throw IllegalArgumentException("Got invalid reading type.")
        }
    }


    private fun generateTemperatureMessages(reading: TemperatureReading): List<String> {
        return listOf("200,Temperature,Result,${reading.temperature},Celcius,${reading.timestamp}")
    }

    private fun generateLightMessages(reading: LightReading): List<String> {
        return listOf("200,Light,Result,${reading.light},%,${reading.timestamp}")
    }

    private fun generateHumidityReading(reading: HumidityReading): List<String> {
        return listOf("200,Humidity,Result,${reading.humidity},%,${reading.timestamp}")
    }

    private fun generatePressureReading(reading: PressureReading): List<String> {
        return listOf("200,Pressure,Result,${reading.pressure},hPa,${reading.timestamp}")
    }

    private fun generateMagnetometerMessages(reading: MagnetometerReading): List<String> {
        val xMessage = "200,Magnetometer X,Result,${reading.magnetometerX},uT,${reading.timestamp}"
        val yMessage = "200,Magnetometer Y,Result,${reading.magnetometerY},uT,${reading.timestamp}"
        val zMessage = "200,Magnetometer Z,Result,${reading.magnetometerZ},uT,${reading.timestamp}"

        return listOf(xMessage, yMessage, zMessage)
    }

    private fun generateAccelerometerMessages(reading: AccelerometerReading): List<String> {
        val xMessage =
            "200,Accelerometer X,Result,${reading.accelerometerX},uT,${reading.timestamp}"
        val yMessage =
            "200,Accelerometer Y,Result,${reading.accelerometerY},uT,${reading.timestamp}"
        val zMessage =
            "200,Accelerometer Z,Result,${reading.accelerometerZ},uT,${reading.timestamp}"

        return listOf(xMessage, yMessage, zMessage)
    }

    private fun generateGyroscopeMessages(reading: GyroscopeReading): List<String> {
        val xMessage = "200,Gyroscope X,Result,${reading.gyroscopeX},uT,${reading.timestamp}"
        val yMessage = "200,Gyroscope Y,Result,${reading.gyroscopeY},uT,${reading.timestamp}"
        val zMessage = "200,Gyroscope Z,Result,${reading.gyroscopeZ},uT,${reading.timestamp}"

        return listOf(xMessage, yMessage, zMessage)
    }

    private fun generateBatteryMesssage(reading: BatteryReading): List<String> {
        return listOf("200,Battery Level,${reading.batteryLevel},%,${reading.timestamp}")
    }

    companion object {

        private const val SERVER_URI = "tcp://aconno.cumulocity.com:1883"

        private const val CLIENT_ID = "AcnSensaClient"

        private const val CUMULOSITY_SUBSCRIBTION_TOPIC = "s/us"
        private const val QUALITY_OF_SERVICE = 0
        private const val RETENTION_POLICY = false

    }
}