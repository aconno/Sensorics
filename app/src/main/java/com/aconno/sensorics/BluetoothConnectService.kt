package com.aconno.sensorics

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
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

    override fun onUnbind(intent: Intent): Boolean {
        isBoundToActivity = false
        return super.onUnbind(intent)
    }

    fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        type: String,
        value: Any
    ) {
        offerACommandToQueue(WriteCommand(serviceUUID, characteristicUUID, type, value))
    }

    private fun offerACommandToQueue(writeCommand: WriteCommand) {
        writeCommandQueue.offer(writeCommand)

        if (writeCommandQueue.size == 1) {
            writeCommandToDevice(writeCommand)
        }
    }

    fun getConnectResults(): Flowable<GattCallbackPayload> {
        return connectionPublisher.toFlowable(BackpressureStrategy.BUFFER)
    }

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


    fun connect(deviceAddress: String) {
        if (isConnected) {
            if (deviceAddress == device?.macAddress) {
                return
            }
        }

        bluetooth.connect(deviceAddress)
    }

    fun disconnect() {
        bluetooth.disconnect()
    }

    fun close() {
        bluetooth.closeConnection()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        close()
        super.onDestroy()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (ACTION_STOP_SERVICE == it.action) {
                if (isBoundToActivity) {
                    disconnect()
                } else {
                    close()
                    stopForeground(true)
                    stopSelf()
                }
                return super.onStartCommand(intent, flags, startId)
            }

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
                    }
                }
            }

            compositeDisposable.add(subscribe)

            it.getStringExtra(EXTRA_DEVICE)?.let {
                device = Gson().fromJson<Device>(it, Device::class.java)?.apply {
                    connect(macAddress)
                }
            }
        }

        if (isConnected) {
            startForeground(SERVICE_ID, makeNotification("Connected"))
        } else {
            startForeground(SERVICE_ID, makeNotification())
        }

        return super.onStartCommand(intent, flags, startId)
    }

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

    private fun getActivityContentPendingIntent(): PendingIntent {
        return Intent(applicationContext, ConnectActivity::class.java).let {
            it.putExtra(ConnectActivity.EXTRA_DEVICE, Gson().toJson(device))
            PendingIntent.getActivity(
                applicationContext,
                1,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

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

    override fun onBind(intent: Intent?): IBinder {
        isBoundToActivity = true
        return mBinder
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