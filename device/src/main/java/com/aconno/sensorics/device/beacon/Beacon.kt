package com.aconno.sensorics.device.beacon

import android.content.Context
import com.aconno.sensorics.device.beacon.v2.BeaconImpl
import com.aconno.sensorics.device.bluetooth.tasks.GenericTask
import com.aconno.sensorics.device.bluetooth.tasks.lock.LockStateRequestCallback
import com.aconno.sensorics.domain.migrate.getArrayOrNull
import com.aconno.sensorics.domain.migrate.getObjectOrNull
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor
import com.google.gson.Gson
import com.google.gson.JsonObject

abstract class Beacon(context: Context, taskProcessor: BluetoothTaskProcessor) {
    /**
     * Beacon parameter list
     */
    abstract val parameters: Parameters

    /**
     * Beacon slot list
     */
    abstract val slots: Slots

    /**
     * Beacon arbitrary data
     */
    abstract val arbitraryData: ArbitraryData

    /**
     * Manufacturer ParameterImpl
     */
    val manufacturer: String
        get() = parameters.getParameterAsString("Manufacturer")

    /**
     * Model ParameterImpl
     */
    val model: String
        get() = parameters.getParameterAsString("Model")

    /**
     * Hardware Version ParameterImpl
     */
    val hwVersion: String
        get() = parameters.getParameterAsString("Hardware version")

    /**
     * Firmware Version ParameterImpl
     */
    val fwVersion: String
        get() = parameters.getParameterAsString("Firmware version")

    /**
     * SDK Version ParameterImpl
     */
    val sdkVersion: String
        get() = parameters.getParameterAsString("SDK version")

    /**
     * FreeRTOS Version ParameterImpl
     */
    val freeRTOSVersion: String
        get() = parameters.getParameterAsString("FreeRTOS version")

    /**
     * MAC Address ParameterImpl
     */
    val mac: String
        get() = parameters.getParameterAsString("MAC")

    /**
     * Supported TX Powers
     */
    val supportedTxPowers: List<Byte>
        get() = parameters.getParameterAsString("Supported TX powers").split(",").map { it.toByte() }

    /**
     * Supported TX Powers String
     */
    val supportedTxPowersString: String
        get() = supportedTxPowers.joinToString(", ")

    /**
     * SlotImpl Count ParameterImpl
     */
    val slotCount: String
        get() = slots.size.toString()

    /**
     * Queues up an unlock task for the beacon
     *
     * @param password specified password
     * @param callback lock state request callback
     */
    abstract fun unlock(password: String, callback: LockStateRequestCallback)

    /**
     * Request the device lock status
     *
     * @param callback lock state request callback
     */
    abstract fun requestDeviceLockStatus(callback: LockStateRequestCallback)

    /**
     * Read all the configuration data
     */
    abstract fun read(onDoneTask: GenericTask? = null)

    /**
     * Write all the configuration data
     *
     * @param full indremental or full write
     */
    abstract fun write(full: Boolean, onDoneTask: GenericTask? = null)

    fun toJson(): JsonObject {
        return JsonObject().apply {
            this.add("parameters", this@Beacon.parameters.toJson())
            this.add("slots", this@Beacon.slots.toJson())
            this.add("arbitraryData", Gson().toJsonTree(arbitraryData))
        }
    }

    @Throws(IllegalArgumentException::class)
    fun loadChangesFromJson(obj: JsonObject) {
        val parameters = obj.getObjectOrNull("parameters")
            ?: throw IllegalArgumentException(
                "Parameters missing!"
            )
        this.parameters.loadChangesFromJson(parameters)

        val slots = obj.getAsJsonObject("slots").getArrayOrNull("slots")
            ?: throw IllegalArgumentException(
                "Slots missing!"
            )
        this.slots.loadChangesFromJson(slots)
    }

    class Factory {
        companion object {
            fun createFromBleDevice(context: Context, taskProcessor: BluetoothTaskProcessor): Beacon {
                return BeaconImpl(context, taskProcessor)
            }
        }
    }
}