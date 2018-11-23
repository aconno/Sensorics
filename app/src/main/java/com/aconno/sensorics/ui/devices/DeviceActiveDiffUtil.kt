package com.aconno.sensorics.ui.devices

import android.support.v7.util.DiffUtil
import com.aconno.sensorics.model.DeviceActive

class DeviceActiveDiffUtil : DiffUtil.ItemCallback<DeviceActive>() {
    override fun areItemsTheSame(p0: DeviceActive, p1: DeviceActive): Boolean {
        return p0.device.macAddress == p1.device.macAddress
    }

    override fun areContentsTheSame(p0: DeviceActive, p1: DeviceActive): Boolean {
        return !(p0.active.xor(p1.active)) &&
                p0.device.name == p1.device.name &&
                !(p0.device.connectable.xor(p1.device.connectable))
    }
}