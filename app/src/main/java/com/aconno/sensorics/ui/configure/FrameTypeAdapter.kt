package com.aconno.sensorics.ui.configure

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.aconno.sensorics.device.beacon.Slot


class FrameTypeAdapter(context: Context, types: Array<Slot.Type>) :
    ArrayAdapter<Slot.Type>(context, android.R.layout.simple_spinner_item, types) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView
            ?: LayoutInflater.from(context).inflate(
                android.R.layout.simple_spinner_dropdown_item,
                parent,
                false
            )
        view.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.name ?: ""
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView
            ?: LayoutInflater.from(context).inflate(
                android.R.layout.simple_spinner_item,
                parent,
                false
            )
        view.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.name ?: ""
        return view
    }
}