package com.aconno.acnsensa.data.mqtt

import android.content.Context
import android.net.Uri
import android.preference.PreferenceManager
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.model.readings.Reading
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

class GoogleCloudPublisher(context: Context) : Publisher {

    //TODO: Refactor

    private val mqttAndroidClient: MqttAndroidClient

    private val messagesQueue: Queue<String> = LinkedList<String>()

    private lateinit var regionPreference: String

    private lateinit var deviceregistryPreference: String

    private lateinit var devicePreference: String

    private lateinit var privatekeyPreference: String

    private lateinit var projectidPreference: String

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences?.let {
            projectidPreference = preferences.getString("projectid_preference", "")
            regionPreference = preferences.getString("region_preference", "")
            deviceregistryPreference = preferences.getString("deviceregistry_preference", "")
            devicePreference = preferences.getString("device_preference", "")
            privatekeyPreference = preferences.getString("privatekey_preference", "")
        }

        mqttAndroidClient = MqttAndroidClient(context, SERVER_URI, getClientID())
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

    private fun showError(s: String) {
        TODO("Please provide an error mechanism")
    }

    private fun getSubscriptionTopic(): String {
        return "/devices/${devicePreference}/events"
    }

    private fun getClientID(): String {
        return "projects/${projectidPreference}/locations/${regionPreference}/registries/${deviceregistryPreference}/devices/${devicePreference}"
    }

    private fun getPrivateKeyData(): ByteArray {
        val uri = Uri.parse(privatekeyPreference)
        val file = File(uri.path)

        val stream = FileInputStream(file)
        val size = stream.available()
        val buffer = ByteArray(size)
        stream.read(buffer)
        stream.close()
        return buffer
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
            getSubscriptionTopic(),
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
        options.password = createJwtRsa(projectidPreference).toCharArray()

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

        val keyBytes = getPrivateKeyData()
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val kf = KeyFactory.getInstance("RSA")

        return jwtBuilder.signWith(SignatureAlgorithm.RS256, kf.generatePrivate(spec)).compact()
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

        private const val CUMULOSITY_SUBSCRIBTION_TOPIC = "s/us"

        private const val QUALITY_OF_SERVICE = 0
        private const val RETENTION_POLICY = false
    }
}