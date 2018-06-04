package com.aconno.acnsensa.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.model.Device
import kotlinx.android.synthetic.main.item_device.view.*

class BeaconAdapter(
    private val beacons: MutableList<Device>,
    private val itemClickListener: ItemClickListener<Device>
) : RecyclerView.Adapter<BeaconAdapter.ViewHolder>() {

    fun addBeacon(beacon: Device) {
        beacons.add(beacon)
        notifyDataSetChanged()
    }

    fun setBeacons(beacons: List<Device>) {
        this.beacons.clear()
        this.beacons.addAll(beacons)
        notifyDataSetChanged()
    }

    fun clearBeacons() {
        beacons.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return beacons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(beacons[position])
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(beacon: Device) {
            view.name.text = beacon.name
            view.mac_address.text = beacon.macAddress
            view.setOnClickListener { itemClickListener.onItemClick(beacon) }
        }
    }
}