package com.aconno.sensorics.adapter.viewpager2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.device_main.DeviceMainFragment

class ViewPagerAdapter(
        fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

    var deviceList = mutableListOf<DeviceActive>()

    set(value) {
        field = value
        notifyDataSetChanged()
    }

    fun updateItemAt(updatedDevice: DeviceActive, position: Int) {
        deviceList[position] = updatedDevice
        notifyItemChanged(position)
    }

    fun removeItemAt(position: Int) {
        deviceList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun insertItemAt(removedDevice: DeviceActive, position: Int) {
        deviceList.add(position, removedDevice)
        notifyItemInserted(position)
    }

    override fun getItem(position: Int): Fragment {
        return DeviceMainFragment.newInstance(deviceList[position].device)
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }
}