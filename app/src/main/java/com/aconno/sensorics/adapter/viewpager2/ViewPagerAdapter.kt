package com.aconno.sensorics.adapter.viewpager2

import androidx.fragment.app.FragmentActivity
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.device_main.DeviceMainFragment
import com.aconno.sensorics.ui.welcome.WelcomeFragment

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    companion object {
        const val ITEM_TYPE_WELCOME = 0
        const val ITEM_TYPE_DEVICE = 1
    }

    private var deviceList: MutableList<DeviceActive> = mutableListOf()

    fun submitList(list: MutableList<DeviceActive>) {
        deviceList = list
        notifyDataSetChanged()
    }

    fun removeItemAt(position: Int) {
        deviceList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemViewType(position: Int) =
        if (position == 0) ITEM_TYPE_WELCOME else ITEM_TYPE_DEVICE

    override fun getItem(position: Int) =
        if (position == 0) WelcomeFragment.newInstance()
        else DeviceMainFragment.newInstance(deviceList[position - 1].device)

    override fun getItemCount(): Int {
        return deviceList.size + 1
    }
}