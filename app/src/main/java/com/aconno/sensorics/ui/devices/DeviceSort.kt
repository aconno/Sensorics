package com.aconno.sensorics.ui.devices

import android.content.SharedPreferences
import com.aconno.sensorics.model.DeviceActive
import java.util.*
import kotlin.Comparator

class DeviceSort {
    var sortByAttribute : SortAttributes? = null
    var sortOrder = SortOrder.ASCENDING

    companion object {
        const val SORT_ATTRIBUTE_KEY = "SORT_ATTRIBUTE_KEY"
        const val SORT_ORDER_KEY = "SORT_ORDER_KEY"
    }

    enum class SortAttributes {
        NAME, MAC_ADDRESS, TIME
    }

    enum class SortOrder {
        ASCENDING,DESCENDING
    }

    fun saveToPreferences(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit()
            .putString(SORT_ATTRIBUTE_KEY,sortByAttribute?.name)
            .putString(SORT_ORDER_KEY,sortOrder.name)
            .apply()
    }

    fun loadFromPreferences(sharedPreferences: SharedPreferences) {
        sortByAttribute = sharedPreferences.getString(SORT_ATTRIBUTE_KEY,null)?.let {
            SortAttributes.valueOf(it)
        } ?: SortAttributes.TIME
        sortOrder = sharedPreferences.getString(SORT_ORDER_KEY,null)?.let {
            SortOrder.valueOf(it)
        } ?: SortOrder.ASCENDING
    }

    fun sortDevices(devices : List<DeviceActive>) : List<DeviceActive> {
        if(sortByAttribute == null) return devices

        val nameComparator = Comparator<DeviceActive> { d1, d2 ->
            val name1 = if(d1!!.device.alias.isNotEmpty()) {
                d1.device.alias
            } else {
                d1.device.name
            }

            val name2 = if(d2!!.device.alias.isNotEmpty()) {
                d2.device.alias
            } else {
                d2.device.name
            }
            name1.compareTo(name2)
        }

        val macAddressComparator = Comparator<DeviceActive> { d1, d2 -> d1!!.device.macAddress.compareTo(d2!!.device.macAddress) }

        val timeComparator = Comparator<DeviceActive> { d1, d2 -> (d1!!.device.timeAdded ?: Date(0)).compareTo(d2!!.device.timeAdded ?: Date(0)) }

        val attributeComparator =
            when(sortByAttribute) {
                SortAttributes.NAME -> nameComparator
                SortAttributes.MAC_ADDRESS -> macAddressComparator
                SortAttributes.TIME -> timeComparator
                null -> return devices
            }

        val comparator =
            if(sortOrder == SortOrder.DESCENDING) {
                Comparator<DeviceActive> { d1, d2 -> -attributeComparator.compare(d1,d2) }
            } else {
                attributeComparator
            }

        return devices.sortedWith(comparator)
    }
}