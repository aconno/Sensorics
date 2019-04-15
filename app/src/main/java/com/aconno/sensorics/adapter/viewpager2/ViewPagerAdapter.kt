package com.aconno.sensorics.adapter.viewpager2

import android.util.SparseArray
import androidx.fragment.app.FragmentActivity
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.device_main.DeviceMainFragment
import com.aconno.sensorics.ui.device_main.ScanStatus
import com.aconno.sensorics.ui.welcome.WelcomeFragment

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    companion object {
        const val ITEM_TYPE_WELCOME = 0
        const val ITEM_TYPE_DEVICE = 1
    }

    /**
     * Keeps track of fragment references
     */
    private val sparseFragmentArray = SparseArray<DeviceMainFragment>()

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
        else getDeviceMainFragment(position)

    private fun getDeviceMainFragment(position: Int): DeviceMainFragment =
        if (sparseFragmentArray[position] != null) {
            sparseFragmentArray[position]
        } else {
            DeviceMainFragment.newInstance(deviceList[position - 1].device).apply {
                sparseFragmentArray.put(position, this)
            }
        }

    /**
     * If ScanStatus is not initialized, this will return null. Instead of creating new one.
     */
    private fun getScanStatusItemOrNull(position: Int): ScanStatus? =
        if (position < sparseFragmentArray.size() && sparseFragmentArray[position] != null) {
            sparseFragmentArray[position]
        } else {
            null
        }

    fun submitStatusChangedList(list: List<DeviceActive>) {
        val sparseArray = sparseFragmentArray
        for (i in 0 until sparseArray.size()) {
            val viewPagerPosition = sparseArray.keyAt(i)
            sparseArray.get(viewPagerPosition)?.let {
                val device = list[viewPagerPosition - 1]
                getScanStatusItemOrNull(viewPagerPosition)?.setStatus(device.active)
            }
        }
    }

    override fun getItemCount(): Int {
        return deviceList.size + 1
    }
}