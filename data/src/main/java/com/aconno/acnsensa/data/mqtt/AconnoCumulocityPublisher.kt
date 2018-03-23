package com.aconno.acnsensa.data.mqtt

import android.content.Context
import com.aconno.acnsensa.domain.Publisher
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

    override fun publish(message: String) {
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
    
    companion object {

        private const val SERVER_URI = "tcp://aconno.cumulocity.com:1883"

        private const val CLIENT_ID = "AcnSensaClient"

        private const val CUMULOSITY_SUBSCRIBTION_TOPIC = "s/us"
        private const val QUALITY_OF_SERVICE = 0
        private const val RETENTION_POLICY = false

    }
}