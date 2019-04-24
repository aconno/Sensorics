package com.aconno.sensorics

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.core.app.TaskStackBuilder
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.device.notification.NotificationFactory
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.ui.connect.ConnectActivity
import com.aconno.sensorics.ui.devicecon.WriteCommand
import com.google.gson.Gson
import dagger.android.DaggerService
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import javax.inject.Inject

/**
 * A foreground service for managin bluetooth connection.
 *
 * Connect - Disconnect - Write Implemented.
 * TODO Implement Read and Increasing mtu size.
 *
 * @property bluetooth Bluetooth
 * @property mBinder LocalBinder
 * @property writeCommandQueue Queue<WriteCommand>
 * @property connectionPublisher BehaviorSubject<GattCallbackPayload>
 * @property isConnected Boolean
 * @property device Device?
 * @property isBoundToActivity Boolean
 * @property compositeDisposable CompositeDisposable
 */
class BluetoothConnectService : DaggerService() {

    @Inject
    lateinit var bluetooth: Bluetooth

    private val mBinder = LocalBinder()
    private val writeCommandQueue: Queue<WriteCommand> = ArrayDeque<WriteCommand>()
    private val connectionPublisher: BehaviorSubject<GattCallbackPayload> = BehaviorSubject.create()

    private var isConnected = false
    private var device: Device? = null
    private var isBoundToActivity = false
    private var compositeDisposable = CompositeDisposable()

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothConnectService {
            return this@BluetoothConnectService
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            //If ConnectActivity is alive. Just Disconnect otherwise kill the service
            if (ACTION_STOP_SERVICE == it.action) {
                if (isBoundToActivity) {
                    disconnect()
                } else {
                    shutDownService()
                }
                return super.onStartCommand(intent, flags, startId)
            }

            //Subscribe to the gatt result flowable
            val subscribe = bluetooth.getGattResults().subscribe { gattCallbackPayload ->
                if (gattCallbackPayload.action != BluetoothGattCallback.ACTION_GATT_CHAR_WRITE) {
                    //Transfer and buffer the last one
                    connectionPublisher.onNext(gattCallbackPayload)
                }

                when {
                    gattCallbackPayload.action == BluetoothGattCallback.ACTION_GATT_SERVICES_DISCOVERED -> {
                        isConnected = true
                        startForeground(SERVICE_ID, makeNotification("Connected"))
                    }
                    gattCallbackPayload.action == BluetoothGattCallback.ACTION_GATT_CHAR_WRITE -> {
                        //remove the first one
                        writeCommandQueue.poll()

                        //write next one
                        writeCommandQueue.peek()?.let { writeCommand ->
                            writeCommandToDevice(writeCommand)
                        }
                    }
                    gattCallbackPayload.action == BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                        isConnected = false
                        startForeground(
                            SERVICE_ID,
                            makeNotificationForDisconnection("Disconnected")
                        )

                        if (!isBoundToActivity) {
                            shutDownService()
                        }
                    }
                }
            }

            compositeDisposable.add(subscribe)

            //Get device from intent
            it.getStringExtra(EXTRA_DEVICE)?.let {
                Gson().fromJson<Device>(it, Device::class.java)?.apply {
                    if (!isConnected || device?.macAddress != macAddress) {
                        connect(macAddress)
                    }

                    device = this
                }
            }
        }

        if (isConnected) {
            //If we have already connection show proper notification
            startForeground(SERVICE_ID, makeNotification("Connected"))
        } else {
            //If we don't have a connection show connecting notificaton
            startForeground(SERVICE_ID, makeNotification())
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        isBoundToActivity = true
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        isBoundToActivity = false
        if (!isConnected) {
            shutDownService()
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        close()
        super.onDestroy()
    }

    private fun shutDownService() {
        close()
        stopForeground(true)
        stopSelf()
    }

    /**
     * Creates notification for Connecting - Connected
     * @param status String
     * @return Notification
     */
    private fun makeNotification(status: String = "Connecting"): Notification {
        return with(NotificationFactory()) {
            val contentIntent = getActivityContentPendingIntent()

            val pStopSelf = getStopIntent(this@BluetoothConnectService).let {
                PendingIntent.getService(
                    applicationContext,
                    0,
                    it,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            }

            makeConnectionNotification(
                applicationContext,
                "Connection : ${device?.macAddress}, Status : $status",
                contentIntent,
                pStopSelf,
                "Disconnect"
            )
        }
    }

    /**
     * Returns pending intent for when user clicks the notification.
     * @return PendingIntent
     */
    private fun getActivityContentPendingIntent(): PendingIntent {
        return Intent(applicationContext, ConnectActivity::class.java).let {
            it.putExtra(ConnectActivity.EXTRA_DEVICE, Gson().toJson(device))

            val taskStackBuilder = TaskStackBuilder.create(this)
            taskStackBuilder.addNextIntentWithParentStack(it)
            //This is Not Nullable because of PendingIntent.FLAG_UPDATE_CURRENT
            taskStackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT)!!
        }
    }

    /**
     * Creates a notification for Disconnected
     * @param status String
     * @return Notification
     */
    private fun makeNotificationForDisconnection(status: String = "Disconnected"): Notification {
        return with(NotificationFactory()) {
            val contentIntent = getActivityContentPendingIntent()

            val pStopSelf = Intent(applicationContext, BluetoothConnectService::class.java).let {
                it.putExtra(ConnectActivity.EXTRA_DEVICE, Gson().toJson(device))
                it.action = ACTION_CONNECT_BLE
                PendingIntent.getService(
                    applicationContext,
                    0,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            makeConnectionNotification(
                applicationContext,
                "Connection : ${device?.macAddress}, Status : $status",
                contentIntent,
                pStopSelf,
                "Connect"
            )
        }
    }

    /**
     * Takes writeCharacteristic request and converts it to the [WriteCommand] object.
     * Then offers it to the Queue
     * @param serviceUUID UUID
     * @param characteristicUUID UUID
     * @param type String
     * @param value Any
     */
    fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        type: String,
        value: Any
    ) {
        offerACommandToQueue(WriteCommand(serviceUUID, characteristicUUID, type, value))
    }

    /**
     * Offers a command to the Queue. If queue has no any command
     * @param writeCommand WriteCommand
     */
    private fun offerACommandToQueue(writeCommand: WriteCommand) {
        writeCommandQueue.offer(writeCommand)

        if (writeCommandQueue.size == 1) {
            writeCommandToDevice(writeCommand)
        }
    }

    /**
     * Writes given command to the device
     * @param writeCommand WriteCommand
     * @return Boolean
     */
    private fun writeCommandToDevice(writeCommand: WriteCommand) =
        if (isConnected) {
            bluetooth.writeCharacteristic(
                writeCommand.serviceUUID,
                writeCommand.charUUID,
                writeCommand.type,
                writeCommand.value
            )
        } else {
            false
        }


    /**
     * Return flowable for GATT events
     * @return Flowable<GattCallbackPayload>
     */
    fun getConnectResults(): Flowable<GattCallbackPayload> {
        return connectionPublisher.toFlowable(BackpressureStrategy.BUFFER)
    }

    /**
     * Connects to the given mac address.
     * If it has already connected returns.
     * @param deviceAddress String
     */
    fun connect(deviceAddress: String) {
        if (isConnected) {
            if (deviceAddress == device?.macAddress) {
                return
            }
        }

        bluetooth.connect(deviceAddress)
    }

    /**
     * Disconnects from previously connected device
     */
    fun disconnect() {
        bluetooth.disconnect()
    }

    /**
     * Closes Ble connection.
     */
    fun close() {
        bluetooth.closeConnection()
    }

    companion object {
        private const val EXTRA_DEVICE = "EXTRA_DEVICE"
        private const val SERVICE_ID = 7562
        private const val ACTION_STOP_SERVICE = "com.aconno.sensorics.STOP_CONNECTION_SERVICE"
        private const val ACTION_CONNECT_BLE = "com.aconno.sensorics.DISCONNECT_BLE"

        fun getStopIntent(context: Context): Intent {
            return Intent(context.applicationContext, BluetoothConnectService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
        }

        fun start(context: Context, serviceConnection: ServiceConnection, device: Device) {
            Intent(context.applicationContext, BluetoothConnectService::class.java).apply {
                putExtra(EXTRA_DEVICE, Gson().toJson(device))

                context.applicationContext.startService(this)
                context.applicationContext.bindService(
                    this,
                    serviceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }
        }
    }
}