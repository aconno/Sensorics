package com.aconno.sensorics.adapter

import android.graphics.drawable.Drawable
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
    private var iconsMap: HashMap<String, String> = hashMapOf()

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

    fun setIcons(icons: HashMap<String, String>) {
        iconsMap = icons
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val device = devices[position]
        val context = parent.context
        val iconPath = iconsMap[device.name]

        if (convertView == null) {
            val newView =
                    LayoutInflater.from(context).inflate(R.layout.item_spinner_device, parent, false)

            if (iconPath == null) {
                newView.image_icon.setImageResource(R.drawable.ic_sensa)
            } else {
                val icon = Drawable.createFromPath(iconPath)
                newView.image_icon.setImageDrawable(icon)
            }

            newView.text_name.text = device.getRealName()
            newView.text_mac_address.text = device.macAddress
            return newView
        } else {
            if (iconPath == null) {
                convertView.image_icon.setImageResource(R.drawable.ic_sensa)
            } else {
                val icon = Drawable.createFromPath(iconPath)
                convertView.image_icon.setImageDrawable(icon)
            }
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