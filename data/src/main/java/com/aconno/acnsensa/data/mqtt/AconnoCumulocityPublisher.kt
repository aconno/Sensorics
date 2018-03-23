package com.aconno.acnsensa.data.mqtt

import android.content.Context
import com.aconno.acnsensa.domain.Publisher
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber

class AconnoCumulocityPublisher(context: Context) : Publisher {

    private val mqttAndroidClient: MqttAndroidClient

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

    override fun publish(message: String) {
        TODO("not implemented")
    }

    companion object {

        private const val SERVER_URI = "tcp://aconno.cumulocity.com:1883"

        private const val CLIENT_ID = "AcnSensaClient"
    }
}