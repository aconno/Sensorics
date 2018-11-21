package com.aconno.sensorics.adapter

import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.aconno.sensorics.R
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.DeviceActive
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_device_with_connect.view.*
import timber.log.Timber


class DeviceActiveAdapter : RecyclerView.Adapter<DeviceActiveAdapter.ViewHolder>() {

    private val devices = mutableListOf<DeviceActive>()

    var iconsMap: HashMap<String, String> = hashMapOf<String, String>()

    fun setDevices(devices: List<DeviceActive>) {
        this.devices.clear()
        this.devices.addAll(devices)
        notifyDataSetChanged()
    }

    fun clearDevices() {
        devices.clear()
        notifyDataSetChanged()
    }

    fun getDevice(position: Int) = devices[position]

    private val onItemClickEvents = PublishSubject.create<DeviceActive>()

    fun getOnItemClickEvents(): Flowable<DeviceActive> =
        onItemClickEvents.toFlowable(BackpressureStrategy.LATEST)

    private val onItemLongClickEvents = PublishSubject.create<DeviceActive>()

    fun getOnItemLongClickEvents(): Flowable<DeviceActive> =
        onItemLongClickEvents.toFlowable(BackpressureStrategy.LATEST)

    private val onConnectClickEvents = PublishSubject.create<DeviceActive>()

    fun getOnConnectClickEvents(): Flowable<DeviceActive> =
        onConnectClickEvents.toFlowable(BackpressureStrategy.LATEST)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_with_connect, parent, false)
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

    fun restoreItem(item: DeviceActive, position: Int) {
        devices.add(position, item)
        notifyItemInserted(position)
    }

    fun setIcon(icons: HashMap<String, String>) {
        iconsMap = icons
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private var viewBackground: RelativeLayout? = null
        var viewForeground: ConstraintLayout? = null

        fun bind(device: DeviceActive) {
            Timber.d("Bind device to view, name: ${device.device.getRealName()}, mac: ${device.device.macAddress}, icon: ${device.device.icon}")

            val iconPath = iconsMap.get(device.device.name)
            val icon = Drawable.createFromPath(iconPath)
            view.image_icon.setImageDrawable(icon)
            view.name.text = device.device.getRealName()
            view.mac_address.text = device.device.macAddress

            view.setOnClickListener { onItemClickEvents.onNext(device) }

            view.btn_connect.visibility = if (device.device.connectable) View.VISIBLE else View.GONE
            view.btn_connect.setOnClickListener { onConnectClickEvents.onNext(device) }

            view.setOnLongClickListener {
                onItemLongClickEvents.onNext(device)
                true
            }

            if (device.active) {
                view.image_icon.alpha = 1f
                view.name.alpha = 1f
                view.mac_address.alpha = 1f
                view.btn_connect.isEnabled = true
            } else {
                view.image_icon.alpha = 0.5f
                view.name.alpha = 0.5f
                view.mac_address.alpha = 0.5f
                view.btn_connect.isEnabled = false
            }

            viewBackground = view.view_background
            viewForeground = view.view_foreground
        }
    }
}