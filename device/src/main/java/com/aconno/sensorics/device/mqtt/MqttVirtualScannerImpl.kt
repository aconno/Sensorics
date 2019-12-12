package com.aconno.sensorics.device.mqtt

import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.mqtt.MqttVirtualScanner
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber

class MqttVirtualScannerImpl : MqttVirtualScanner {
    private var isScanning: Boolean = false
    private var topics: MutableSet<String> = mutableSetOf()

    private var clients: MutableList<MqttClient> = mutableListOf()
    private var clientTopics: MutableMap<MqttClient, MqttVirtualScannerCallback> = mutableMapOf()


    val defaultMqttOptions = MqttConnectOptions().apply {
        this.isAutomaticReconnect = true
        this.isCleanSession = true
    }

    private val scanResults: PublishSubject<ScanResult> = PublishSubject.create()

    override fun addSource(
        serverUri: String,
        clientId: String
    ) {
        val client = MqttClient(
            serverUri,
            clientId
        )

        val callback = MqttVirtualScannerCallback(client)
        client.setCallback(callback)

        clients.add(client)
        clientTopics[client] = callback
        if (isScanning) {
            client.connect(defaultMqttOptions)
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
        }.let { filteredClients ->
            filteredClients.forEach {
                if (it.isConnected) {
                    it.disconnectForcibly()
                }
                clientTopics.remove(it)
                clients.remove(it)
            }
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
        clients.filter {
            it.isConnected
        }.forEach {
            it.disconnect()
        }
    }

    override fun startScanning(devices: List<Device>) {
        clients.filter {
            !it.isConnected
        }.forEach {
            it.connect(defaultMqttOptions)
        }
        addDevicesToScanFor(devices)
        isScanning = true
    }

    override fun getScanResults(): Flowable<ScanResult> {
        return scanResults.toFlowable(BackpressureStrategy.LATEST).observeOn(Schedulers.io())
    }

    inner class MqttVirtualScannerCallback(var client: MqttClient, var subscribedTopics: MutableSet<String> = mutableSetOf()) : MqttCallbackExtended { // TODO: This might never get garbage collected
        fun checkSubscribedTopics(topics: Set<String>) {
            (topics - subscribedTopics).let { missingTopics ->
                missingTopics.forEach {
                    client.subscribe(it)
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

        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
            Timber.d("%s: Connection completed", client.serverURI)
            checkSubscribedTopics()
        }

        override fun connectionLost(cause: Throwable?) {
            Timber.e(cause)
            subscribedTopics.clear()
        }

        override fun messageArrived(topic: String, msg: MqttMessage) {
            val timestamp = System.currentTimeMillis()
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
}