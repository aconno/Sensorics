package com.aconno.sensorics.adapter.viewpager

import android.util.SparseArray
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.device_main.DeviceMainFragment
import com.aconno.sensorics.ui.device_main.ScanStatus
import com.aconno.sensorics.ui.welcome.WelcomeFragment
import timber.log.Timber

class ViewPagerAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager) {

    /**
     * Keeps track of fragment references
     */
    private val sparseFragmentArray = SparseArray<DeviceMainFragment>()
    private var deviceList: MutableList<DeviceActive> = mutableListOf()

    init {
        fragmentManager.fragments.forEachIndexed { index, fragment ->
            fragment.takeIf {
                it is DeviceMainFragment
            }?.let {
                sparseFragmentArray.put(index, it as DeviceMainFragment)
            }
        }
    }


    fun submitList(list: MutableList<DeviceActive>) {
        deviceList = list
        notifyDataSetChanged()
    }

    fun removeItemAt(position: Int) {
        deviceList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int) =
        if (position == 0) WelcomeFragment.newInstance()
        else {
            Timber.d("$position")
            getDeviceMainFragment(position)
        }

    private fun getDeviceMainFragment(position: Int): DeviceMainFragment =
        DeviceMainFragment.newInstance(deviceList[position - 1].device).apply {
            sparseFragmentArray.put(position, this)
        }

    /**
     * If ScanStatus is not initialized, this will return null. Instead of creating new one.
     */
    private fun getScanStatusItemOrNull(position: Int): ScanStatus? =
        if (position <= sparseFragmentArray.size() && sparseFragmentArray[position] != null) {
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

    override fun getPageTitle(position: Int): CharSequence? = if (position == 0) "Welcome" else ""

    override fun getCount(): Int {
        return deviceList.size + 1
    }
}