package com.aconno.acnsensa.data.publisher

import android.content.Context
import com.aconno.acnsensa.data.converter.PublisherDataConverter
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.MqttPublish
import com.aconno.acnsensa.domain.interactor.filter.Reading
import com.aconno.acnsensa.domain.model.Device
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.nio.charset.Charset
import java.util.*

class MqttPublisher(
    context: Context,
    private val mqttPublish: MqttPublish,
    private val listDevices: List<Device>
) : Publisher {

    private val mqttAndroidClient: MqttAndroidClient = MqttAndroidClient(
        context,
        mqttPublish.url, mqttPublish.clientId
    )

    private val messagesQueue: Queue<String> = LinkedList<String>()

    private var testConnectionCallback: Publisher.TestConnectionCallback? = null

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
    }

    override fun test(testConnectionCallback: Publisher.TestConnectionCallback) {
        this.testConnectionCallback = testConnectionCallback
        connect()
    }

    private fun showError(s: String) {
        TODO("Please provide an error mechanism")
    }

    override fun getPublishData(): BasePublish {
        return mqttPublish
    }

    override fun isPublishable(device: Device): Boolean {
        return System.currentTimeMillis() > (mqttPublish.lastTimeMillis + mqttPublish.timeMillis)
                && listDevices.contains(device)
    }

    override fun publish(reading: Reading) {
        val messages = PublisherDataConverter.convert(reading)
        for (message in messages) {
            Timber.tag("Publisher Mqtt ")
                .d("${mqttPublish.name} publishes from ${reading.device}")
            publish(message)
        }
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
            mqttPublish.topic,
            message.toByteArray(Charset.defaultCharset()),
            mqttPublish.qos,
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
                        testConnectionCallback?.onSuccess()
                        mqttAndroidClient.close()
                        mqttAndroidClient.disconnect()
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Timber.e(exception, "Failed to connect to server")
                    testConnectionCallback?.onFail()
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

        options.userName = mqttPublish.username
        options.password = mqttPublish.password.toCharArray()

        return options
    }

    private fun publishMessagesFromQueue() {
        while (messagesQueue.isNotEmpty()) {
            publish(messagesQueue.poll())
        }
    }

    override fun closeConnection() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.close()
    }

    companion object {
        private const val RETENTION_POLICY = false
    }
}