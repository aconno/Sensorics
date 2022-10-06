package com.aconno.sensorics.ui.settings.publishers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ItemDeviceSwitchBinding
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.DeviceRelationModel


class DeviceSelectAdapter(
    private val itemList: List<DeviceRelationModel>,
    private val itemCheckChangeListener: ItemCheckChangeListener? = null
) : RecyclerView.Adapter<DeviceSelectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ItemDeviceSwitchBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
        itemCheckChangeListener?.let {
            holder.binding.switchDevice.setOnCheckedChangeListener { _, isChecked ->
                it.onItemCheckedChange(position, isChecked)
            }
        }
    }

    inner class ViewHolder(val binding: ItemDeviceSwitchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(deviceRelationModel: DeviceRelationModel) {
            binding.name.text = deviceRelationModel.getRealName()
            binding.macAddress.text = deviceRelationModel.macAddress
            binding.switchDevice.isChecked = deviceRelationModel.related
        }
    }

    interface ItemCheckChangeListener {
        fun onItemCheckedChange(position: Int, isChecked: Boolean)
    }

}