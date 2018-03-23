package com.aconno.acnsensa.data

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.nio.charset.Charset


/**
 * @author aconno
 */
class MqttHelper(context: Context) {
    var mqttAndroidClient: MqttAndroidClient

    internal val serverUri = "tcp://aconno.cumulocity.com:1883"

    private val clientId = "ExampleAndroidClient"
    private val subscriptionTopic = "sensor"

    private val username = "aconno/miroslav@aconno.de"
    private val password = "aconnoadmin"

    init {
        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("mqtt", s)
            }

            override fun connectionLost(throwable: Throwable) {

            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("Mqtt", mqttMessage.toString())
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.e(
                    "Hello", "Got the payload ${iMqttDeliveryToken.message.payload.toString(
                        Charset.defaultCharset()
                    )}"
                )

            }
        })
        connect()
    }

    fun setCallback(callback: MqttCallbackExtended) {
        mqttAndroidClient.setCallback(callback)
    }

    private fun connect() {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.userName = username
        mqttConnectOptions.password = password.toCharArray()

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {

                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                    //subscribeToTopic()
                    publishMessage()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString())
                }
            })


        } catch (ex: MqttException) {
            ex.printStackTrace()
        }

    }

    private fun publishMessage() {
        mqttAndroidClient.publish(
            subscriptionTopic,
            "Hellochen".toByteArray(Charset.defaultCharset()),
            1,
            false
        )
    }

    private fun subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.w("Mqtt", "Subscribed!")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.w("Mqtt", "Subscribed fail!")
                }
            })

        } catch (ex: MqttException) {
            System.err.println("Exceptionst subscribing")
            ex.printStackTrace()
        }

    }
}

fun startMqtt(applicationContext: Context) {
    val mqttHelper = MqttHelper(applicationContext)
    mqttHelper.setCallback(object : MqttCallbackExtended {
        override fun connectComplete(b: Boolean, s: String) {

        }

        override fun connectionLost(throwable: Throwable) {

        }

        @Throws(Exception::class)
        override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
            Log.w("Debug", mqttMessage.toString())
            Log.e("Result", mqttMessage.toString())
        }

        override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {

        }
    })
}
