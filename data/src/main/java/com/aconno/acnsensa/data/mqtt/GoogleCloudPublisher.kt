package com.aconno.acnsensa.data.mqtt

import android.content.Context
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.model.readings.Reading
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

class GoogleCloudPublisher(context: Context) : Publisher {

    //TODO: Refactor

    private val mqttAndroidClient: MqttAndroidClient

    private val messagesQueue: Queue<String> = LinkedList<String>()

    private val privateKeyByteArray = loadData("rsa_private_pkcs8", context)

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

    override fun publish(reading: Reading) {
        val messages = GoogleCloudDataConverter.convert(reading)
        for (message in messages) {
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
            GOOGLE_SUBSCRIPTION_TOPIC,
            message.toByteArray(Charset.defaultCharset()),
            QUALITY_OF_SERVICE,
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
        options.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1

        val sslProperties = Properties()
        sslProperties.setProperty("com.ibm.ssl.protocol", "TLSv1.2")
        options.sslProperties = sslProperties

        options.userName = "unused"
        options.password = createJwtRsa("loyal-rookery-204211").toCharArray()

        return options
    }

    @Throws(Exception::class)
    private fun createJwtRsa(projectId: String): String {
        // Create a JWT to authenticate this device. The device will be disconnected after the token
        // expires, and will have to reconnect with a new token. The audience field should always be set
        // to the GCP project id.
        val jwtBuilder = Jwts.builder()
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 20))
            .setAudience(projectId)

        val keyBytes = privateKeyByteArray
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val kf = KeyFactory.getInstance("RSA")

        return jwtBuilder.signWith(SignatureAlgorithm.RS256, kf.generatePrivate(spec)).compact()
    }

    private fun loadData(fileName: String, context: Context): ByteArray? {
        try {
            val stream = context.assets.open(fileName)
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            return buffer
        } catch (e: IOException) {
            // Handle exceptions here
        }

        return null
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

        private const val SERVER_URI = "ssl://mqtt.googleapis.com:8883"

        //TODO: Simplify this
        private const val CLIENT_ID =
            "projects/loyal-rookery-204211/locations/us-central1/registries/ACNSensaRegistry/devices/ACNSensaDemo_01"

        private const val CUMULOSITY_SUBSCRIBTION_TOPIC = "s/us"
        private const val GOOGLE_SUBSCRIPTION_TOPIC = "/devices/ACNSensaDemo_01/events"

        private const val QUALITY_OF_SERVICE = 0
        private const val RETENTION_POLICY = false

    }
}