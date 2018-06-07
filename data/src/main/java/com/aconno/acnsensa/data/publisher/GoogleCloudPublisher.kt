package com.aconno.acnsensa.data.publisher

import android.content.Context
import android.net.Uri
import com.aconno.acnsensa.data.converter.PublisherDataConverter
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.SensorReading
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

class GoogleCloudPublisher(
    context: Context,
    private val googlePublish: GooglePublish,
    private val listDevices: List<Device>
) : Publisher {

    //TODO: Refactor
    private val mqttAndroidClient: MqttAndroidClient

    private val jwtByteArray: ByteArray

    private val messagesQueue: Queue<String> = LinkedList<String>()

    private var testConnectionCallback: Publisher.TestConnectionCallback? = null

    init {
        jwtByteArray = getPrivateKeyData(context)
        mqttAndroidClient = MqttAndroidClient(
            context,
            SERVER_URI, getClientID()
        )
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

    private fun getSubscriptionTopic(): String {
        return "/devices/${googlePublish.device}/events"
    }

    private fun getClientID(): String {
        return "projects/${googlePublish.projectId}/locations/${googlePublish.region}/registries/${googlePublish.deviceRegistry}/devices/${googlePublish.device}"
    }

    private fun getPrivateKeyData(context: Context): ByteArray {
        val uri = Uri.parse(googlePublish.privateKey)
        val stream = context.contentResolver.openInputStream(uri)

        val size = stream.available()
        val buffer = ByteArray(size)
        stream.read(buffer)
        stream.close()
        return buffer
    }

    override fun getPublishData(): BasePublish {
        return googlePublish
    }

    override fun isPublishable(device: Device): Boolean {
        return System.currentTimeMillis() > (googlePublish.lastTimeMillis + googlePublish.timeMillis)
                && listDevices.contains(device)
    }

    override fun publish(reading: SensorReading) {
        val messages = PublisherDataConverter.convert(reading)
        for (message in messages) {
            Timber.tag("Publisher Google")
                .d("${googlePublish.name} publishes from ${reading.device}")
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

        options.userName = "unused"
        options.password = createJwtRsa(googlePublish.projectId).toCharArray()

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

        val keyBytes = jwtByteArray
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
        private const val QUALITY_OF_SERVICE = 0
        private const val RETENTION_POLICY = false
    }
}