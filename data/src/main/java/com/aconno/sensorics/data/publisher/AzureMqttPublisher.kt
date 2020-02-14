package com.aconno.sensorics.data.publisher

import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.Sync
import com.aconno.sensorics.domain.repository.SyncRepository
import com.microsoft.azure.sdk.iot.device.*
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus
import timber.log.Timber
import java.util.*

class AzureMqttPublisher (
        private val azureMqttPublish: AzureMqttPublish,
        private val listDevices: List<Device>,
        private val syncRepository: SyncRepository) : Publisher {

    private val lastSyncs: MutableMap<Pair<String, String>, Long> =
            syncRepository.getSync(PUBLISHER_UNIQUE_ID_PREFIX + azureMqttPublish.id)
                    .map { Pair(it.macAddress, it.advertisementId) to it.lastSyncTimestamp }
                    .toMap()
                    .toMutableMap()

    private val dataStringConverter: DataStringConverter = DataStringConverter(azureMqttPublish.dataString)

    private val deviceClient : DeviceClient

    @Volatile
    private var connectionOpened = false

    init {
        deviceClient = DeviceClient(buildConnectionString(), IotHubClientProtocol.MQTT)
        deviceClient.registerConnectionStatusChangeCallback(
                { status, _, throwable, _ ->

                    Timber.d("Azure publisher connection status update: $status")

                    throwable?.printStackTrace()

                    when (status) {
                        IotHubConnectionStatus.DISCONNECTED -> {
                            Timber.d("Azure publisher connection lost")
                            connectionOpened = false
                        }
                        IotHubConnectionStatus.DISCONNECTED_RETRYING -> {
                            Timber.d("Azure publisher connection temporarily lost, retrying to connect")
                        }
                        IotHubConnectionStatus.CONNECTED -> {
                            Timber.d("Azure publisher connected successfully")
                            connectionOpened = true
                        }
                        else -> {}
                    }
                },
                null
        )
    }

    override fun publish(readings: List<Reading>) {
        if (readings.isNotEmpty() && isPublishable(readings)) {
            if(!connectionOpened) {
                connect()
                if(!connectionOpened) { //if failed to connect
                    return
                }
            }

            val messages = dataStringConverter.convert(readings)
            for (message in messages) {
                Timber.tag("Publisher Azure Mqtt ")
                        .d("${azureMqttPublish.name} publishes from ${readings[0].device}")
                publish(message)
            }

            val reading = readings.first()
            val time = System.currentTimeMillis()
            Timber.e(
                    "Sync done at: ${Pair(
                            reading.device.macAddress,
                            reading.advertisementId
                    )} $time"
            )
            syncRepository.save(
                    Sync(
                            PUBLISHER_UNIQUE_ID_PREFIX + azureMqttPublish.id,
                            reading.device.macAddress,
                            reading.advertisementId,
                            time
                    )
            )
            lastSyncs[Pair(reading.device.macAddress, reading.advertisementId)] = time
        }
    }

    private fun publish(messageText: String) {
        publishMessage(messageText)
    }

    private fun connect() {
        try {
            deviceClient.open()
            connectionOpened = true
        } catch (ex : Exception) {
            Timber.d("Failed to connect to publisher.")
            ex.printStackTrace()
        }

    }


    private fun publishMessage(messageText: String) {
        try {
            val message = Message(messageText)
            message.messageId = UUID.randomUUID().toString()

            deviceClient.sendEventAsync(message,
                    { responseStatus, _ -> Timber.d("Message response status: $responseStatus") }
                    , null)
        } catch (e: Exception) {
            Timber.d("Message failed to send")
            e.printStackTrace()
        }
    }

    private fun isPublishable(readings: List<Reading>): Boolean {
        val reading = readings.firstOrNull()
        val latestTimestamp =
                lastSyncs[Pair(reading?.device?.macAddress, reading?.advertisementId)] ?: 0

        return System.currentTimeMillis() - latestTimestamp > this.azureMqttPublish.timeMillis
                && reading != null && listDevices.contains(reading.device)
    }

    override fun closeConnection() {
        deviceClient.closeNow()
        connectionOpened = false
    }

    override fun getPublishData(): BasePublish {
        return azureMqttPublish
    }

    private fun buildConnectionString() : String {
        return String.format(CONNECTION_STRING_FORMAT,
                azureMqttPublish.iotHubName,
                azureMqttPublish.deviceId,
                azureMqttPublish.sharedAccessKey)
    }

    override fun test(testConnectionCallback: Publisher.TestConnectionCallback) {
        val testClient = DeviceClient(buildConnectionString(), IotHubClientProtocol.MQTT)
        try {
            testClient.open()
            testConnectionCallback.onConnectionSuccess()
        } catch (e: Exception) {
            testConnectionCallback.onConnectionFail(e)
        } finally {
            testClient.closeNow()
        }

    }

    companion object {
        private const val CONNECTION_STRING_FORMAT = "HostName=%s.azure-devices.net;DeviceId=%s;SharedAccessKey=%s"
        private const val PUBLISHER_UNIQUE_ID_PREFIX = "azure"
    }


}