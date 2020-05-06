package com.aconno.sensorics.data.publisher

import android.content.Context
import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.Sync
import com.aconno.sensorics.domain.repository.SyncRepository
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.nio.charset.Charset
import java.util.*

class MqttPublisher(
    context: Context,
    publish: MqttPublish,
    listDevices: List<Device>,
    syncRepository: SyncRepository
) : Publisher<MqttPublish>(
    publish, listDevices, syncRepository
) {
    private val mqttAndroidClient: MqttAndroidClient = MqttAndroidClient(
        context,
        publish.url, publish.clientId
    )

    private var testConnectionCallback: TestConnectionCallback? = null
    private val dataStringConverter: DataStringConverter


    init {
        mqttAndroidClient.setCallback(
            object : MqttCallbackExtended {

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

        dataStringConverter = DataStringConverter(publish.dataString)
    }

    override fun test(testConnectionCallback: TestConnectionCallback) {
        this.testConnectionCallback = testConnectionCallback
        connect()
    }

    override fun publish(readings: List<Reading>) {
        if (readings.isNotEmpty() && isPublishable(readings)) {

            Timber.d("size is ${readings.size}")
            val messages = dataStringConverter.convert(readings)
            for (message in messages) {
                Timber.tag("Publisher Mqtt ")
                    .d("${publish.name} publishes from ${readings[0].device}")
                publish(message)
            }

            val reading = readings.first()
            val time = System.currentTimeMillis()
            Timber.e(
                "Sync done at: ${Pair(
                    reading.device.macAddress,
                    reading.advertisementId
                )} $time"
            )
            syncRepository.save(
                Sync(
                    "mqtt" + publish.id,
                    reading.device.macAddress,
                    reading.advertisementId,
                    time
                )
            )
            lastSyncs[Pair(reading.device.macAddress, reading.advertisementId)] = time
        }
    }

    private fun publish(message: String) {
        if (mqttAndroidClient.isConnected) {
            publishMessage(message)
        } else {
            connect()
            messageQueue.add(message)
        }
    }

    private fun publishMessage(message: String) {
        mqttAndroidClient.publish(
            publish.topic,
            message.toByteArray(Charset.defaultCharset()),
            publish.qos,
            RETENTION_POLICY
        )
    }

    private fun connect() {
        val options = getConnectOptions()
        try {
            mqttAndroidClient.connect(options, null, object : IMqttActionListener {

                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Timber.d("Successful connect to server, token: %s", asyncActionToken)
                    publishMessagesFromQueue()

                    if (testConnectionCallback != null) {
                        testConnectionCallback?.onConnectionSuccess()
                        testConnectionCallback = null
                        closeConnection()
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Timber.e(exception, "Failed to connect to server")
                    testConnectionCallback?.onConnectionFail(exception)
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
        options.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1

        val sslProperties = Properties()
        sslProperties.setProperty("com.ibm.ssl.protocol", "TLSv1.2")
        options.sslProperties = sslProperties

        if(publish.username.trim().isNotEmpty()) {
            options.userName = publish.username
        }
        if(publish.password.trim().isNotEmpty()) {
            options.password = publish.password.toCharArray()
        }

        return options
    }

    private fun publishMessagesFromQueue() {
        while (messageQueue.isNotEmpty()) {
            messageQueue.poll()?.let {
                publish(it)
            }
        }
    }

    override fun closeConnection() {
        try {
            mqttAndroidClient.unregisterResources()
            mqttAndroidClient.close()
            mqttAndroidClient.disconnect()
        } catch (ex: Exception) {
            //Do-Nothing
        }
    }

    companion object {
        private const val RETENTION_POLICY = false
    }
}