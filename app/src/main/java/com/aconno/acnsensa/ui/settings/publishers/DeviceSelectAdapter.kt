package com.aconno.acnsensa.ui.settings.publishers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Switch
import android.widget.TextView
import com.aconno.acnsensa.R
import com.aconno.acnsensa.model.DeviceRelationModel


class DeviceSelectAdapter(
    context: Context,
    itemList: List<DeviceRelationModel>,
    private val itemCheckChangeListener: ItemCheckChangeListener? = null
) :
    ArrayAdapter<DeviceRelationModel>(context, 0, itemList) {

    // View lookup cache
    private class ViewHolder {
        internal var nameView: TextView? = null
        internal var macAddressView: TextView? = null
        internal var checkBoxView: Switch? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // Get the data item for this position
        val deviceRelationModel = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.item_device_switch, parent, false)
            viewHolder.macAddressView = convertView!!.findViewById(R.id.mac_address)
            viewHolder.nameView = convertView.findViewById(R.id.name)
            viewHolder.checkBoxView = convertView.findViewById(R.id.switch_device)
            // Cache the viewHolder object inside the fresh view
            convertView.tag = viewHolder
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.checkBoxView!!.setOnCheckedChangeListener(null)
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.macAddressView!!.text = deviceRelationModel.name
        viewHolder.nameView!!.text = deviceRelationModel.macAddress
        viewHolder.checkBoxView!!.isChecked = deviceRelationModel.related
        // Return the completed view to render on screen

        itemCheckChangeListener?.let {
            viewHolder.checkBoxView!!.setOnCheckedChangeListener { _, isChecked ->
                it.onItemCheckedChange(position, isChecked)
            }
        }

        return convertView
    }

    interface ItemCheckChangeListener {
        fun onItemCheckedChange(position: Int, isChecked: Boolean)
    }

}