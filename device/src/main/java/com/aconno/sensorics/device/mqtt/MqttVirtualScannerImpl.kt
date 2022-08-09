package com.aconno.sensorics.device.mqtt

import android.content.Context
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.mqtt.MqttVirtualScanner
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import timber.log.Timber

class MqttVirtualScannerImpl(val context: Context) : MqttVirtualScanner {
    private var isScanning: Boolean = false
    private var topics: MutableSet<String> = mutableSetOf()

    private var clients: MutableList<MqttAndroidClient> = mutableListOf()
    private var clientTopics: MutableMap<MqttAndroidClient, MqttVirtualScannerCallback> = mutableMapOf()
    private var clientConnectOptions : MutableMap<MqttAndroidClient, MqttConnectOptions> = mutableMapOf()
    private var clientScanningSources : MutableMap<MqttAndroidClient, MqttVirtualScanningSource> = mutableMapOf()

    private val scanResults: PublishSubject<ScanResult> = PublishSubject.create()

    override var scanningConnectionCallback : MqttVirtualScanner.ConnectionCallback? = null

    override fun addSource(
        source: MqttVirtualScanningSource
    ) {

        val client = MqttAndroidClient(
            context,
            source.getUri(),
            source.clientId,
            MemoryPersistence()
        )


        val callback = MqttVirtualScannerCallback(client, source.qualityOfService)
        client.setCallback(callback)

        clients.add(client)
        clientTopics[client] = callback
        clientConnectOptions[client] = getMqttConnectOptionsForSource(source, SCANNING_CONNECTION_TIMEOUT_SECONDS)
        clientScanningSources[client] = source


        if (isScanning) {
            client.connect(clientConnectOptions[client], null, connectionCallback)
        }
    }

    override fun removeSource(
        serverUri: String,
        clientId: String?
    ) {
        clients.filter { client ->
            client.serverURI == serverUri &&
                if (clientId == null) {
                    true
                } else {
                    client.clientId == clientId
                }
        }.forEach {
            disconnectSafe(it)
            clientTopics.remove(it)
            clients.remove(it)
            clientConnectOptions.remove(it)
            clientScanningSources.remove(it)
        }
    }

    private fun getMqttConnectOptionsForSource(source : MqttVirtualScanningSource, connectionTimeout : Int) : MqttConnectOptions {
        val mqttOptions = MqttConnectOptions()
        mqttOptions.isCleanSession = true
        mqttOptions.connectionTimeout = connectionTimeout

        if(source.username.trim().isNotEmpty()) {
            mqttOptions.userName = source.username
        }
        if(source.password.trim().isNotEmpty()) {
            mqttOptions.password = source.password.toCharArray()
        }


        return mqttOptions
    }

    override fun testConnection(testConnectionCallback: MqttVirtualScanner.TestConnectionCallback,
                                mqttVirtualScanningSource: MqttVirtualScanningSource) {

        val client = MqttAndroidClient(
                context,
                mqttVirtualScanningSource.getUri(),
                mqttVirtualScanningSource.clientId,
                MemoryPersistence()
        )

        val mqttOptions =
                getMqttConnectOptionsForSource(mqttVirtualScanningSource, TEST_CONNECTION_TIMEOUT_SECONDS)

        val connectionCallback: IMqttActionListener = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                testConnectionCallback.onConnectionSuccess()
                closeConnection(client)
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable?) {
                exception?.printStackTrace()
                testConnectionCallback.onConnectionFail(exception)
                closeConnection(client)
            }
        }

        try {
            client.connect(mqttOptions, null, connectionCallback)
        } catch (ex : Exception) {
            testConnectionCallback.onConnectionFail(ex)
            closeConnection(client)
        }
    }

    private fun closeConnection(client : MqttAndroidClient) {
        try {
            client.unregisterResources()
            client.close()
            client.disconnect()
        } catch (ex : Exception) {
            ex.printStackTrace()
        }
    }


    fun disconnectSafe(client: MqttAndroidClient) {
        if (client.isConnectedSafe()) {
            client.disconnect(null, disconnectCallback)
        } else {
            clientTopics[client]?.let {
                it.disconnect = true
            }
        }
    }

    override fun clearSources() {
        while(clients.isNotEmpty()) { //iterating using forEach causes concurrent modification exception so it has to be implemented this way
            removeSource(clients[0].serverURI, null)
        }
    }

    override fun addDevicesToScanFor(devices: List<Device>) {
        topics.addAll(devices.map { it.macAddress })
        checkSubscribedTopics()
    }

    override fun addDeviceToScanFor(device: Device) {
        topics.add(device.macAddress)
        checkSubscribedTopics()
    }

    override fun removeDevicesToScanFor(devices: List<Device>) {
        topics.removeAll(devices.map { it.macAddress })
        checkSubscribedTopics()
    }

    override fun removeDeviceToScanFor(device: Device) {
        topics.remove(device.macAddress)
        checkSubscribedTopics()
    }

    private fun checkSubscribedTopics() {
        clientTopics.forEach { (_, callback) ->
            callback.checkSubscribedTopics(topics)
        }
    }

    override fun stopScanning() {
        clients.forEach {
            disconnectSafe(it)
        }
    }

    override fun startScanning(devices: List<Device>) {
        clients.forEach {
            it.connect(clientConnectOptions[it], null, connectionCallback)
        }
        addDevicesToScanFor(devices)
        isScanning = true
    }

    override fun getScanResults(): Flowable<ScanResult> {
        return scanResults.toFlowable(BackpressureStrategy.LATEST).observeOn(Schedulers.io())
    }

    private val connectionCallback: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken) {
            clientScanningSources[asyncActionToken.client]?.let {
                scanningConnectionCallback?.onConnectionSuccess(it)
            }
            Timber.d("MQTT ${asyncActionToken.client.serverURI}: connected")
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable?) {
            clientScanningSources[asyncActionToken.client]?.let {
                scanningConnectionCallback?.onConnectionFail(it,exception)
            }

            Timber.d("MQTT ${asyncActionToken.client.serverURI}: connection failure: $exception")
        }
    }

    private val disconnectCallback: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken) {
            Timber.d("MQTT ${asyncActionToken.client.serverURI}: disconnected")
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable?) {
            Timber.d("MQTT ${asyncActionToken.client.serverURI}: disconnect failure: $exception")
        }
    }

    inner class MqttVirtualScannerCallback(var client: MqttAndroidClient, var qos:Int = 0, var subscribedTopics: MutableSet<String> = mutableSetOf()) : MqttCallbackExtended {
        var disconnect: Boolean = false

        fun checkSubscribedTopics(topics: Set<String>) {
            if (client.isConnectedSafe()) {
                (topics - subscribedTopics).let { missingTopics ->
                    missingTopics.forEach {
                        client.subscribe(it, qos, null, object : IMqttActionListener {
                            override fun onSuccess(asyncActionToken: IMqttToken?) {
                                Timber.d("Subscribed to $it successfully")
                            }

                            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                                Timber.e(exception, "Failed to subscribe to $it successfully")
                                subscribedTopics.remove(it)
                            }
                        })
                        subscribedTopics.add(it)
                    }
                }
                (subscribedTopics - topics).let { extraTopics ->
                    extraTopics.forEach {
                        client.unsubscribe(it)
                        subscribedTopics.remove(it)
                    }
                }
            }
        }

        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
            Timber.d("%s: Connection completed", client.serverURI)
            if (!disconnect) {
                checkSubscribedTopics()
            } else {
                disconnect = false
                disconnectSafe(client)
            }
        }

        override fun connectionLost(cause: Throwable?) {
            Timber.e(cause?.message ?: "")
            subscribedTopics.clear()
        }

        override fun messageArrived(topic: String, msg: MqttMessage) {
            val timestamp = System.currentTimeMillis()
            @Suppress("UnnecessaryVariable")
            val macAddress = topic
            val rssi = 0
            val rawData = msg.payload

            val virtualScanResult = ScanResult(timestamp, macAddress, rssi, rawData)

            scanResults.onNext(virtualScanResult)

            Timber.d("%s: Message arrived, topic: %s, message: %s", client.serverURI, topic, msg)
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            // TODO
        }

    }

    companion object {
        private const val TEST_CONNECTION_TIMEOUT_SECONDS = 5
        private const val SCANNING_CONNECTION_TIMEOUT_SECONDS = 15
    }
}

fun MqttAndroidClient.isConnectedSafe(): Boolean {
    return try {
        this.isConnected
    } catch (e: IllegalArgumentException) {
        false
    }
}