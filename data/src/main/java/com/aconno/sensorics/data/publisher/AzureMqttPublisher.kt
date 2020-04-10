package com.aconno.sensorics.data.publisher

import android.os.AsyncTask
import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.Sync
import com.aconno.sensorics.domain.repository.SyncRepository
import com.microsoft.azure.sdk.iot.device.DeviceClient
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol
import com.microsoft.azure.sdk.iot.device.Message
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus
import timber.log.Timber
import java.util.*

class AzureMqttPublisher(
    publish: AzureMqttPublish,
    listDevices: List<Device>,
    syncRepository: SyncRepository
) : Publisher<AzureMqttPublish>(
    publish, listDevices, syncRepository
) {
    private val dataStringConverter: DataStringConverter =
        DataStringConverter(publish.dataString)

    private var deviceClient: DeviceClient? = null

    @Volatile
    private var connectionOpened = false

    @Volatile
    private var tryingToConnect = false

    init {
        initClient()
    }

    private fun initClient() {
        deviceClient = try {
            DeviceClient(buildConnectionString(), IotHubClientProtocol.MQTT)
        } catch (ex: Exception) {
            Timber.d("Failed to instantiate device client: ${ex.message}")
            null
        }
        deviceClient?.registerConnectionStatusChangeCallback(
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
                    else -> {
                    }
                }
            },
            null
        )
    }

    override fun publish(readings: List<Reading>) {
        if (deviceClient == null) {
            Timber.d("Unable to publish readings since device client has not been instantiated.")
            return
        }

        if (readings.isNotEmpty() && isPublishable(readings)) {
            val messages = dataStringConverter.convert(readings)
            for (message in messages) {
                Timber.tag("Publisher Azure Mqtt ")
                    .d("${publish.name} publishes from ${readings[0].device}")
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
                    publish.type.type + publish.id,
                    reading.device.macAddress,
                    reading.advertisementId,
                    time
                )
            )
            lastSyncs[Pair(reading.device.macAddress, reading.advertisementId)] = time
        }
    }


    private fun publish(messageText: String) {
        if (connectionOpened) {
            publishMessage(messageText)
        } else {
            connectAsync()
            messageQueue.add(messageText)
        }
    }

    private fun connectAsync() {
        if (tryingToConnect || connectionOpened) return

        ConnectAsyncTask(this).execute()
    }

    private fun connect() {
        if (deviceClient == null) {
            connectionOpened = false
            return
        }

        tryingToConnect = true

        try {
            deviceClient?.open()
            connectionOpened = true
        } catch (ex: Exception) {
            Timber.d("Failed to connect to publisher.")
            ex.printStackTrace()
        }

        tryingToConnect = false
    }

    class ConnectAsyncTask(private val publisher: AzureMqttPublisher) :
        AsyncTask<Void, Void, Boolean>() {


        override fun doInBackground(vararg params: Void?): Boolean {
            publisher.connect()
            return publisher.connectionOpened
        }

        override fun onPostExecute(result: Boolean?) {
            if (result == true) {
                publisher.publishMessagesFromQueue()
            }
        }

    }

    private fun publishMessagesFromQueue() {
        while (messageQueue.isNotEmpty()) {
            messageQueue.poll()?.let {
                publish(it)
            }
        }
    }


    private fun publishMessage(messageText: String) {
        try {
            val message = Message(messageText)
            message.messageId = UUID.randomUUID().toString()

            deviceClient?.sendEventAsync(message,
                { responseStatus, _ -> Timber.d("Message response status: $responseStatus") }
                , null)
        } catch (e: Exception) {
            Timber.d("Message failed to send")
            e.printStackTrace()
        }
    }

    override fun closeConnection() {
        try {
            deviceClient?.closeNow()
        } catch (ex: Exception) {
            Timber.d("Exception while closing connection: ${ex.message}")
        }
        connectionOpened = false
    }

    private fun buildConnectionString(): String {
        return String.format(
            CONNECTION_STRING_FORMAT,
            publish.iotHubName,
            publish.deviceId,
            publish.sharedAccessKey
        )
    }

    override fun test(testConnectionCallback: Publisher.TestConnectionCallback) {
        val testClient: DeviceClient
        try {
            testClient = DeviceClient(buildConnectionString(), IotHubClientProtocol.MQTT)
        } catch (ex: Exception) {
            testConnectionCallback.onConnectionFail(ex)
            return
        }
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
        private const val CONNECTION_STRING_FORMAT =
            "HostName=%s.azure-devices.net;DeviceId=%s;SharedAccessKey=%s"
    }


}