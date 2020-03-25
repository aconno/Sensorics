package com.aconno.sensorics.device.beacon.baseimpl

import com.aconno.sensorics.domain.UUIDProvider

abstract class ArbitraryDataBaseImpl(size: Int = 0) : ArbitraryData(size) {

    override operator fun set(key: String, value: String): Boolean {
        val oldValue: String? = put(key, value)
        val newAvailable: Int = capacity - getSerializedSize()

        return if (newAvailable < 0) {
            oldValue?.let {
                put(key, oldValue)
            } ?: remove(key)
            available.value = newAvailable
            false
        } else {
            available.value = newAvailable
            true
        }
    }

    override fun setAll(newMap: Map<String, String>):Boolean {
        val newAvailable = capacity - getSerializedSize(newMap)
        return if (newAvailable<0){
            false
        }else{
            this.clear()
            this.putAll(newMap)
            available.value = newAvailable
            true
        }
    }

    override fun removeEntry(key: String): String? {
        val value: String? = super.remove(key)
        available.value = capacity - getSerializedSize()
        return value
    }

    protected abstract fun getSerializedSize(map: Map<String, String> = this): Int

    protected abstract fun serialize(map: Map<String, String>):ByteArray

    companion object {
        val DEFAULT_ARBITRARY_DATA_SERVICE_UUID = UUIDProvider.provideFullUUID("E000")
        val DEFAULT_ARBITRARY_DATA_CHARACTERISTIC_UUID = UUIDProvider.provideFullUUID("E001")
    }
}