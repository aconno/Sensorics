package com.aconno.sensorics.ui.settings.publishers

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.model.DeviceRelationModel
import kotlinx.android.synthetic.main.item_device_switch.view.*


class DeviceSelectAdapter(
    private val itemList: List<DeviceRelationModel>,
    private val itemCheckChangeListener: ItemCheckChangeListener? = null
) : RecyclerView.Adapter<DeviceSelectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_device_switch, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
        itemCheckChangeListener?.let {
            holder.view.switch_device!!.setOnCheckedChangeListener { _, isChecked ->
                it.onItemCheckedChange(position, isChecked)
            }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(deviceRelationModel: DeviceRelationModel) {
            view.name.text = deviceRelationModel.getRealName()
            view.mac_address.text = deviceRelationModel.macAddress
            view.switch_device.isChecked = deviceRelationModel.related
        }
    }

    interface ItemCheckChangeListener {
        fun onItemCheckedChange(position: Int, isChecked: Boolean)
    }

}