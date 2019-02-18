package com.aconno.sensorics.ui.configure

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aconno.bluetooth.beacon.Beacon
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.item_arbitrary_data.view.*
import kotlinx.android.synthetic.main.parameter_name.view.*

class ArbitraryDataAdapter(val beacon: Beacon) :
    RecyclerView.Adapter<ArbitraryDataAdapter.ListItem>() {
    val TYPE_PARAMETER_TEXT = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItem {
        return when (viewType) {
            TYPE_PARAMETER_TEXT -> ParameterItemText(parent)
            else -> throw IllegalArgumentException("This shouldn't happen!")
        }
    }


    override fun onBindViewHolder(holder: ListItem, position: Int) {
        holder.bind(beacon.abstractDataMapped.toList()[position])
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_PARAMETER_TEXT
    }


    override fun getItemCount(): Int = beacon.abstractDataMapped.size

    abstract class ListItem(open val parent: ViewGroup, open val resource: Int) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(resource, parent, false)
        ) {
        abstract val type: Int
        abstract fun bind(parameter: Pair<String, String>)
    }

    abstract inner class ParameterItem(override val parent: ViewGroup, override val resource: Int) :
        ListItem(parent, resource) {
        override val type: Int = -1

        override fun bind(parameter: Pair<String, String>) {
            itemView.tv_parameter_name.text = parameter.first
        }
    }

    inner class ParameterItemText(override val parent: ViewGroup) : ParameterItem(
        parent, R.layout.item_arbitrary_data
    ), TextWatcher {
        override fun bind(parameter: Pair<String, String>) {
            super.bind(parameter)
            itemView.et_parameter_text.setText(parameter.second)
            itemView.et_parameter_text.addTextChangedListener(this)
            itemView.btn_delete.setOnClickListener {
                beacon.abstractDataMapped.remove(parameter.first)
                notifyDataSetChanged()
            }
        }

        override fun afterTextChanged(s: Editable) {
            if (s.toString().isEmpty()) return
            beacon.abstractDataMapped[beacon.abstractDataMapped.toList()[adapterPosition].first] =
                s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}