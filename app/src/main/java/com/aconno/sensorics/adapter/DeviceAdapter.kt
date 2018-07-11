package com.aconno.sensorics.adapter

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Device
import kotlinx.android.synthetic.main.item_device.view.*
import timber.log.Timber
import android.widget.RelativeLayout


class DeviceAdapter(
    private val devices: MutableList<Device>,
    private val itemClickListener: ItemClickListener<Device>,
    private var longItemClickListener: LongItemClickListener<Device>? = null
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    fun addDevice(device: Device) {
        devices.add(device)
        notifyDataSetChanged()
    }

    fun deleteDevice(device: Device) {
        devices.remove(device)
        notifyDataSetChanged()
    }

    fun setDevices(devices: List<Device>) {
        this.devices.clear()
        this.devices.addAll(devices)
        notifyDataSetChanged()
    }

    fun clearDevices() {
        devices.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    fun removeItem(position: Int) {
        devices.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Device, position: Int) {
        devices.add(position, item)
        notifyItemInserted(position)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        var viewBackground: RelativeLayout? = null
        var viewForeground: ConstraintLayout? = null

        fun bind(device: Device) {
            Timber.d("Bind device to view, name: ${device.name}, mac: ${device.macAddress}, icon: ${device.icon}")
            val iconId = view.context.resources.getIdentifier(
                device.icon,
                "drawable",
                view.context.packageName
            )
            view.image_icon.setImageResource(iconId)
            view.name.text = if (device.alias.isEmpty()) device.name else device.alias
            view.mac_address.text = device.macAddress
            view.setOnClickListener { itemClickListener.onItemClick(device) }
            longItemClickListener?.let {
                view.setOnLongClickListener {
                    longItemClickListener?.onLongClick(device)
                    true
                }
            }

            viewBackground = view.view_background
            viewForeground = view.view_foreground
        }
    }
}