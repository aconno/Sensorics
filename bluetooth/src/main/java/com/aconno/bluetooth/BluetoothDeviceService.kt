package com.aconno.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.aconno.bluetooth.beacon.Beacon
import timber.log.Timber

class BluetoothDeviceService : Service() {
    private val binder = LocalBinder()
    private var bluetoothDeviceImpl: BluetoothDeviceImpl? = null

    fun connectToBluetoothDevice(
        loadingTasksUIInterface: LoadingTasksUIInterface,
        macAddress: String
    ) {
        Timber.d("Called")
        val remoteDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress)
        bluetoothDeviceImpl = BluetoothDeviceImpl(this, remoteDevice)
        bluetoothDeviceImpl?.connect(false, object : BluetoothGattCallback() {
            override fun onDeviceDisconnected(device: BluetoothDevice) {
                Timber.wtf("Disconnected somehow")
            }

            override fun onServicesDiscovered(device: BluetoothDevice) {
                super.onServicesDiscovered(device)

                val beacon = Beacon(device, address = macAddress, rssi = -1)
                val taskListener = object : TasksCompleteListener() {
                    override fun onTaskComplete(tasksCompleted: Int, tasksTotal: Int) {
                        Timber.e("Task Complete")
                        loadingTasksUIInterface.onTaskComplete(tasksCompleted, tasksTotal)
                    }

                    override fun onTasksComplete() {
                        Timber.e("All Tasks Complete")
                        loadingTasksUIInterface.onTasksComplete(beacon)
                    }
                }

                bluetoothDeviceImpl?.addTasksCompleteListener(taskListener)

                loadingTasksUIInterface.onTasksCancelled()

                beacon.requestDeviceLockStatus(object :
                    Beacon.LockStateTask.LockStateRequestTaskCallback {
                    override fun onDeviceLockStateRead(unlocked: Boolean) {
                        if (unlocked) {
                            beacon.read()
                        } else {
                            throw NotImplementedError("Not implemented on FW size fully yet")
                        }
                    }
                })
            }
        })
    }

    fun saveConfig(tasksCompleteListener: TasksCompleteListener) {
        bluetoothDeviceImpl?.addTasksCompleteListener(tasksCompleteListener)
    }

    fun clearQueue() {
        bluetoothDeviceImpl?.queue?.clear()
    }

    fun disconnect() {
        clearQueue()
        bluetoothDeviceImpl?.disconnect()
    }

    interface LoadingTasksUIInterface {
        fun onTaskComplete(tasksCompleted: Int, tasksTotal: Int)
        fun onTasksComplete(beacon: Beacon)
        fun onTasksCancelled()
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothDeviceService {
            return this@BluetoothDeviceService
        }
    }

    override fun onUnbind(intent: Intent): Boolean {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.

        disconnect()
        return super.onUnbind(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
}