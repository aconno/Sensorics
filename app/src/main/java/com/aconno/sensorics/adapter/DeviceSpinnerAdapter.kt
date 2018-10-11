package com.aconno.sensorics.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.getRealName
import kotlinx.android.synthetic.main.item_spinner_device.view.*

class DeviceSpinnerAdapter : SpinnerAdapter, BaseAdapter() {

    private val devices = mutableListOf<Device>()

    fun getDevices(): List<Device> = devices

    fun setDevices(devices: List<Device>) {
        this.devices.clear()
        this.devices.addAll(devices)
        notifyDataSetChanged()
    }

    fun getDevice(position: Int): Device {
        return devices[position]
    }

    fun getDevicePosition(device: Device) = devices.indexOf(device)

    override fun getCount(): Int {
        return devices.size
    }

    override fun getItem(position: Int): Device {
        return devices[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val device = devices[position]
        val context = parent.context
        if (convertView == null) {
            val newView =
                LayoutInflater.from(context).inflate(R.layout.item_spinner_device, parent, false)
            val iconId =
                context.resources.getIdentifier(device.icon, "drawable", context.packageName)
            newView.image_icon.setImageResource(iconId)
            newView.text_name.text = device.getRealName()
            newView.text_mac_address.text = device.macAddress
            return newView
        } else {
            val iconId =
                context.resources.getIdentifier(device.icon, "drawable", context.packageName)
            convertView.image_icon.setImageResource(iconId)
            convertView.text_name.text = device.getRealName()
            convertView.text_mac_address.text = device.macAddress
            return convertView
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}