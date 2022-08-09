package com.aconno.sensorics.data.publisher

import android.content.Context
import android.net.Uri
import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.Sync
import com.aconno.sensorics.domain.repository.SyncRepository
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
    publish: GooglePublish,
    listDevices: List<Device>,
    syncRepository: SyncRepository
) : Publisher<GooglePublish>(
    publish, listDevices, syncRepository
) {

    //TODO: Refactor
    private val mqttAndroidClient: MqttAndroidClient

    private val jwtByteArray: ByteArray

    private var testConnectionCallback: TestConnectionCallback? = null

    private val dataStringConverter: DataStringConverter

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

        dataStringConverter = DataStringConverter(publish.dataString)
    }

    override fun test(testConnectionCallback: TestConnectionCallback) {
        this.testConnectionCallback = testConnectionCallback
        connect()
    }

    private fun getSubscriptionTopic(): String {
        return "/devices/${publish.device}/events"
    }

    private fun getClientID(): String {
        return "projects/${publish.projectId}/locations/${publish.region}/registries/${publish.deviceRegistry}/devices/${publish.device}"
    }

    private fun getPrivateKeyData(context: Context): ByteArray {
        val uri = Uri.parse(publish.privateKey)
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            ByteArray(stream.available()).apply {
                stream.read(this)
            }
        } ?: byteArrayOf()
    }

    override fun publish(readings: List<Reading>) {
        if (readings.isNotEmpty() && isPublishable(readings)) {
            val messages = dataStringConverter.convert(readings)
            for (message in messages) {
                Timber.tag("Publisher Google Cloud ")
                    .d("${publish.name} publishes from ${readings[0].device}")
                publish(message)
            }

            val reading = readings.first()
            val time = System.currentTimeMillis()
            syncRepository.save(
                Sync(
                    "google" + publish.id,
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

        options.userName = "unused"
        options.password = createJwtRsa(publish.projectId).toCharArray()

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
        private const val SERVER_URI = "ssl://mqtt.googleapis.com:8883"
        private const val QUALITY_OF_SERVICE = 0
        private const val RETENTION_POLICY = false
    }
}