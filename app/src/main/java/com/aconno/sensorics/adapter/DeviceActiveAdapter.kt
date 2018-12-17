package com.aconno.sensorics.adapter

import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.aconno.sensorics.R
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.devices.DeviceActiveDiffUtil
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_device.view.*
import timber.log.Timber
import java.util.*


class DeviceActiveAdapter : RecyclerView.Adapter<DeviceActiveAdapter.ViewHolder>() {

    private val asyncListDiffer = AsyncListDiffer<DeviceActive>(this, DeviceActiveDiffUtil())
    private lateinit var devices: MutableList<DeviceActive>

    init {
        asyncListDiffer.submitList(listOf())
    }

    var iconsMap: HashMap<String, String> = hashMapOf()

    fun setDevices(newList: List<DeviceActive>) {
        devices = mutableListOf()
        devices.addAll(newList)
        asyncListDiffer.submitList(devices)
    }

    fun updateActiveDevices(activeList: List<DeviceActive>) {
        activeList.forEachIndexed { index, deviceActive ->
            asyncListDiffer.currentList.find { deviceActive == it }
                ?.let {
                    it.active = deviceActive.active
                    notifyItemChanged(index)
                }
        }
    }

    fun getDevice(position: Int) = asyncListDiffer.currentList[position]

    private val onItemClickEvents = PublishSubject.create<DeviceActive>()

    fun getOnItemClickEvents(): Flowable<DeviceActive> =
        onItemClickEvents.toFlowable(BackpressureStrategy.LATEST)

    private val onItemLongClickEvents = PublishSubject.create<DeviceActive>()

    fun getOnItemLongClickEvents(): Flowable<DeviceActive> =
        onItemLongClickEvents.toFlowable(BackpressureStrategy.LATEST)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    fun removeItem(position: Int) {
        devices.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: DeviceActive, position: Int) {
        devices.add(position, item)
        notifyItemInserted(position)
    }

    fun setIcons(icons: HashMap<String, String>) {
        iconsMap = icons
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private var viewBackground: RelativeLayout? = null
        var viewForeground: ConstraintLayout? = null

        fun bind(device: DeviceActive) {
            Timber.d("Bind device to view, name: ${device.device.getRealName()}, mac: ${device.device.macAddress}, icon: ${device.device.icon}")

            val iconPath = iconsMap[device.device.name]
            if (iconPath == null) {
                view.image_icon.setImageResource(R.drawable.ic_sensa)
            } else {
                val icon = Drawable.createFromPath(iconPath)
                view.image_icon.setImageDrawable(icon)
            }

            view.name.text = device.device.getRealName()
            view.mac_address.text = device.device.macAddress

            view.setOnClickListener { onItemClickEvents.onNext(device) }

            view.setOnLongClickListener {
                onItemLongClickEvents.onNext(device)
                true
            }

            if (device.active) {
                view.image_icon.alpha = 1f
                view.name.alpha = 1f
                view.mac_address.alpha = 1f
            } else {
                view.image_icon.alpha = 0.5f
                view.name.alpha = 0.5f
                view.mac_address.alpha = 0.5f
            }

            viewBackground = view.view_background
            viewForeground = view.view_foreground
        }
    }
}