package com.aconno.sensorics.device.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import com.aconno.sensorics.device.bluetooth.tasks.GenericTask
import com.aconno.sensorics.domain.migrate.addAllFirst
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.scanning.*
import timber.log.Timber
import java.util.*

class BluetoothTaskProcessorImpl(
    private val bluetooth: Bluetooth
) : BluetoothTaskProcessor {
    private var connected: Boolean = false
    private var queue: Deque<Task> = ArrayDeque()


    override fun accept(payload: GattCallbackPayload) {
        Timber.i(payload.action)

        when (payload.action) {
            BluetoothGattCallback.ACTION_GATT_SERVICES_DISCOVERED -> {
                connected = true
                processQueue()
            }
            BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                connected = false
            }
            BluetoothGattCallback.ACTION_GATT_MTU_CHANGED -> {
                (queue.peek() as? MtuTask)?.let { task ->
                    queue.pop() // TODO: DO THIS BETTER

                    (payload.payload as? Int)?.let { mtu ->
                        try {
                            task.onBluetoothSuccess(bluetooth, mtu)
                        } catch (e: Exception) {
                            task.onError(bluetooth, e)
                        }
                    } ?: task.onError(
                        bluetooth,
                        IllegalStateException("Wrong payload type: ${payload.payload}")
                    )

                    queue.addAllFirst(task.taskQueue)
                }

                processQueue()
            }
            BluetoothGattCallback.ACTION_DATA_AVAILABLE,
            BluetoothGattCallback.ACTION_GATT_DESCRIPTOR_READ -> {
                (queue.peek() as? ReadTask)?.let { task ->
                    queue.pop() // TODO: DO THIS BETTER

                    val data = payload.payload

                    when (data) {
                        is BluetoothGattCharacteristic -> data.value
                        is BluetoothGattDescriptor -> data.value
                        else -> null
                    }?.let { value ->
                        try {
                            task.onBluetoothSuccess(bluetooth, value)
                        } catch (e: Exception) {
                            task.onError(bluetooth, e)
                        }
                    } ?: task.onError(
                        bluetooth,
                        IllegalStateException("Wrong payload type: ${payload.payload}")
                    )

                    queue.addAllFirst(task.taskQueue)
                }

                processQueue()
            }
            BluetoothGattCallback.ACTION_GATT_CHAR_WRITE,
            BluetoothGattCallback.ACTION_GATT_DESCRIPTOR_WRITE -> {
                (queue.peek() as? WriteTask)?.let { task ->
                    queue.pop() // TODO: DO THIS BETTER

                    try {
                        task.onBluetoothSuccess(bluetooth)
                    } catch (e: Exception) {
                        task.onError(bluetooth, e)
                    }

                    queue.addAllFirst(task.taskQueue)
                }

                processQueue()
            }
            BluetoothGattCallback.ACTION_DATA_AVAILABLE_FAIL,
            BluetoothGattCallback.ACTION_GATT_CHAR_WRITE_FAIL,
            BluetoothGattCallback.ACTION_GATT_DESCRIPTOR_READ_FAIL,
            BluetoothGattCallback.ACTION_GATT_DESCRIPTOR_WRITE_FAIL -> {
                queue.peek()?.let { task ->
                    task.onError(
                        bluetooth,
                        IllegalStateException("Error code: ${payload.payload as Int?}")
                    )

                    queue.pop()
                }
            }
            else -> {
                return
            }
        }
    }

    override fun queueTask(task: Task): Boolean {
        return if (queue.add(task)) {
            processQueue()
            true
        } else {
            false
        }
    }

    override fun queueTasks(tasks: List<Task>): Boolean {
        return if (queue.addAllFirst(tasks)) {
            processQueue()
            true
        } else {
            false
        }
    }

    private fun processQueue() {
        // Do not process queue if we are not connected
        if (!connected) {
            return
        }

        // Get first task in queue if available
        val task: Task = queue.peek() ?: return

        // Check that the task isn't already being processed
        if (task.active) {
            return
        }

        // Mark task as active
        task.active = true

        // Run pre execution callback
        task.onPreExecute()

        // Execute
        Timber.d("Executing: ${task.name}")
        try {
            val success: Boolean = task.execute(bluetooth)

            if(success) {
                if (task is GenericTask) {
                    task.onSuccess()
                    queue.addAllFirst(task.taskQueue)
                    queue.pop()
                }
            } // TODO: HANDLE THIS
        } catch (e: Exception) {
            task.onError(bluetooth, e)
            queue.pop()
        }

        // Worst case it exits after task active check, needed if task errors, or is generic
        processQueue()
    }
}