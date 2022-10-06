package com.aconno.sensorics.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ItemScandeviceBinding
import com.aconno.sensorics.domain.model.ScanDevice
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ScanDeviceAdapter : RecyclerView.Adapter<ScanDeviceAdapter.ViewHolder>() {

    private val devices = mutableListOf<ScanDevice>()
    private val clickedDeviceStream = PublishSubject.create<ScanDevice>()
    private val iconPathHashMap = hashMapOf<String, String>()

    fun addScanDevice(scanDevice: ScanDevice) {
        val index = devices.indexOf(scanDevice)
        if (index == -1) {
            devices.add(scanDevice)
            notifyItemInserted(devices.size - 1)
        } else {
            devices[index] = scanDevice
            notifyItemChanged(index)
        }
    }

    fun hasIconPath(deviceName: String): Boolean {
        return iconPathHashMap.containsKey(deviceName)
    }

    fun addIconPath(deviceName: String, iconPath: String) {
        iconPathHashMap[deviceName] = iconPath
    }

    fun removeScanDevice(scanDevice: ScanDevice) {
        val index = devices.indexOf(scanDevice)
        if (index >= 0 && index < devices.size) {
            devices.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun getClickedDeviceStream(): Observable<ScanDevice> {
        return clickedDeviceStream
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ItemScandeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    inner class ViewHolder(val binding: ItemScandeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(scanDevice: ScanDevice) {

            if (!hasIconPath(scanDevice.device.name)) {
                binding.imageIcon.setImageResource(R.drawable.ic_sensa)
            } else {
                val icon = Drawable.createFromPath(iconPathHashMap[scanDevice.device.name])
                binding.imageIcon.setImageDrawable(icon)
            }

            binding.textName.text = scanDevice.device.name
            binding.textMacAddress.text = scanDevice.device.macAddress
            binding.textRssi.text = scanDevice.rssi.toString()
            binding.root.setOnClickListener {
                clickedDeviceStream.onNext(scanDevice)
            }
        }
    }
}