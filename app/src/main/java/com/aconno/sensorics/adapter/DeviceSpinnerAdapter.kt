package com.aconno.sensorics.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ItemSpinnerDeviceBinding
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.getRealName

class DeviceSpinnerAdapter : SpinnerAdapter, BaseAdapter() {

    private lateinit var binding: ItemSpinnerDeviceBinding

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
        val iconPath = iconsMap[device.name]

        val binding =
            ItemSpinnerDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        if (convertView == null) {

            if (iconPath == null) {
                binding.imageIcon.setImageResource(R.drawable.ic_sensa)
            } else {
                val icon = Drawable.createFromPath(iconPath)
                binding.imageIcon.setImageDrawable(icon)
            }

            binding.textName.text = device.getRealName()
            binding.textMacAddress.text = device.macAddress
            return binding.root

        } else {

            if (iconPath == null) {
                binding.imageIcon.setImageResource(R.drawable.ic_sensa)
            } else {
                val icon = Drawable.createFromPath(iconPath)
                binding.imageIcon.setImageDrawable(icon)
            }
            binding.textName.text = device.getRealName()
            binding.textMacAddress.text = device.macAddress

            //TODO: see if this is the right view to return
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