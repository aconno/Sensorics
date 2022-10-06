package com.aconno.sensorics.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ItemDeviceBinding
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.DeviceActive
import timber.log.Timber
import java.util.*


class DeviceActiveAdapter(
    itemSelectedListener: ItemSelectedListener<DeviceActive>?,
    clickListener: ItemClickListener<DeviceActive>,
    longClickListener: ItemLongClickListener<DeviceActive>?
) : SelectableRecyclerViewAdapter<DeviceActive, String, DeviceActiveAdapter.ViewHolder>(
    mutableListOf(), itemSelectedListener, clickListener, longClickListener
) {

    override fun getKeyForItem(item: DeviceActive): String {
        return item.device.macAddress
    }

    var iconsMap: HashMap<String, String> = hashMapOf()

    fun setDevices(newList: List<DeviceActive>) {
        setItems(newList)
    }

    fun updateActiveDevices(activeList: List<DeviceActive>) {
        internalItems.forEachIndexed { index, deviceActive ->
            val oldState = deviceActive.active
            deviceActive.active = activeList.find { deviceActive == it }?.active ?: false
            if (oldState != deviceActive.active) {
                notifyItemChanged(index)
            }
        }
    }

    fun getDevice(position: Int) = getItem(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun restoreItem(item: DeviceActive, position: Int) {
        addItemAtPosition(item, position)
    }

    fun setIcons(icons: HashMap<String, String>) {
        iconsMap = icons
    }

    inner class ViewHolder(val binding: ItemDeviceBinding) :
        SelectableRecyclerViewAdapter<DeviceActive, String, ViewHolder>.ViewHolder(binding.root) {

        private var viewBackground: RelativeLayout? = null
        var viewForeground: ConstraintLayout? = null

        override fun bind(item: DeviceActive) {
            Timber.d("Bind device to view, name: ${item.device.getRealName()}, mac: ${item.device.macAddress}, icon: ${item.device.icon}")

            with(binding.cbItemSelected) {
                visibility = if (isItemSelectionEnabled) View.VISIBLE else View.GONE
                isChecked = isItemSelected(item)
            }

            val iconPath = iconsMap[item.device.name]
            if (iconPath == null) {
                binding.imageIcon.setImageResource(R.drawable.ic_sensa)
            } else {
                val icon = Drawable.createFromPath(iconPath)
                binding.imageIcon.setImageDrawable(icon)
            }

            binding.name.text = item.device.getRealName()
            binding.macAddress.text = item.device.macAddress

            binding.root.setOnClickListener {
                if (isItemSelectionEnabled) {
                    binding.cbItemSelected.isChecked = !binding.cbItemSelected.isChecked
                    setSelected(item, binding.cbItemSelected.isChecked)
                } else {
                    onItemClick(item)
                }
            }
            binding.cbItemSelected.setOnClickListener { _ ->
                setSelected(item, binding.cbItemSelected.isChecked)
            }

            binding.root.setOnLongClickListener {
                onItemLongClick(item)
                true
            }

            if (item.active) {
                binding.imageIcon.alpha = 1f
                binding.name.alpha = 1f
                binding.macAddress.alpha = 1f
            } else {
                binding.imageIcon.alpha = 0.5f
                binding.name.alpha = 0.5f
                binding.macAddress.alpha = 0.5f
            }

            viewBackground = binding.viewBackground
            viewForeground = binding.viewForeground
        }
    }

}