package com.aconno.sensorics.adapter.viewpager2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.device_main.DeviceMainFragment

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val deviceList: MutableList<DeviceActive>
) :
    FragmentStateAdapter(fragmentActivity) {
    fun removeItemAt(position: Int) {
        deviceList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItem(position: Int): Fragment {
        return DeviceMainFragment.newInstance(deviceList[position].device)
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }
}