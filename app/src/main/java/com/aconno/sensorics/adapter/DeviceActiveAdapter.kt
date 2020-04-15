package com.aconno.sensorics.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import com.aconno.sensorics.R
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.devices.DeviceActiveDiffUtil
import kotlinx.android.synthetic.main.item_device.view.*
import kotlinx.android.synthetic.main.item_device.view.cb_item_selected
import timber.log.Timber
import java.util.*


class DeviceActiveAdapter(
    itemSelectedListener: ItemSelectedListener<DeviceActive>?,
    clickListener: ItemClickListener<DeviceActive>,
    longClickListener: ItemLongClickListener<DeviceActive>?
) : SelectableRecyclerViewAdapter<DeviceActive,String,DeviceActiveAdapter.ViewHolder>(
    mutableListOf(),itemSelectedListener,clickListener,longClickListener
) {

    private val asyncListDiffer = AsyncListDiffer<DeviceActive>(this, DeviceActiveDiffUtil())

    init {
        asyncListDiffer.submitList(listOf())
    }

    override fun getKeyForItem(item: DeviceActive): String {
        return item.device.macAddress
    }

    var iconsMap: HashMap<String, String> = hashMapOf()

    fun setDevices(newList: List<DeviceActive>) {
        setItems(newList)
        asyncListDiffer.submitList(newList)
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

    fun restoreItem(item: DeviceActive, position: Int) {
        addItemAtPosition(item,position)
    }

    fun setIcons(icons: HashMap<String, String>) {
        iconsMap = icons
    }

    inner class ViewHolder(val view: View) : SelectableRecyclerViewAdapter<DeviceActive, String, ViewHolder>.ViewHolder(view) {

        private var viewBackground: RelativeLayout? = null
        var viewForeground: ConstraintLayout? = null

        override fun bind(item: DeviceActive) {
            Timber.d("Bind device to view, name: ${item.device.getRealName()}, mac: ${item.device.macAddress}, icon: ${item.device.icon}")

            with(itemView.cb_item_selected) {
                visibility = if (isItemSelectionEnabled) View.VISIBLE else View.GONE
                isChecked = isItemSelected(item)
            }

            val iconPath = iconsMap[item.device.name]
            if (iconPath == null) {
                view.image_icon.setImageResource(R.drawable.ic_sensa)
            } else {
                val icon = Drawable.createFromPath(iconPath)
                view.image_icon.setImageDrawable(icon)
            }

            view.name.text = item.device.getRealName()
            view.mac_address.text = item.device.macAddress

            view.setOnClickListener {
                if (isItemSelectionEnabled) {
                    itemView.cb_item_selected.isChecked = !itemView.cb_item_selected.isChecked
                    setSelected(item, itemView.cb_item_selected.isChecked)
                } else {
                    onItemClick(item)
                }
            }
            itemView.cb_item_selected.setOnClickListener { _ ->
                setSelected(item, itemView.cb_item_selected.isChecked)
            }

            view.setOnLongClickListener {
                onItemLongClick(item)
                true
            }

            if (item.active) {
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