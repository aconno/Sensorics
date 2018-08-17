package com.aconno.sensorics.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.ScanDevice
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_scandevice.view.*

class ScanDeviceAdapter : RecyclerView.Adapter<ScanDeviceAdapter.ViewHolder>() {

    private val devices = mutableListOf<ScanDevice>()
    private val clickedDeviceStream = PublishSubject.create<ScanDevice>()

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

    fun removeScanDevice(scanDevice: ScanDevice) {
        val index = devices.indexOf(scanDevice)
        devices.removeAt(index)
        notifyItemRemoved(index)
    }

    fun getClickedDeviceStream(): Observable<ScanDevice> {
        return clickedDeviceStream
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_scandevice, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(scanDevice: ScanDevice) {
            val iconId = view.context.resources.getIdentifier(
                scanDevice.device.icon,
                "drawable",
                view.context.packageName
            )
            view.image_icon.setImageResource(iconId)
            view.text_name.text = scanDevice.device.name
            view.text_mac_address.text = scanDevice.device.macAddress
            view.text_rssi.text = scanDevice.rssi.toString()
            view.setOnClickListener {
                clickedDeviceStream.onNext(scanDevice)
            }
        }
    }
}