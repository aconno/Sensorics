package com.aconno.sensorics.adapter.viewpager

import android.util.SparseArray
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.find
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.device_main.DeviceMainFragment
import com.aconno.sensorics.ui.device_main.ScanStatus
import com.aconno.sensorics.ui.welcome.WelcomeFragment
import timber.log.Timber
import androidx.viewpager.widget.PagerAdapter
import com.aconno.sensorics.keyOf


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
        val tempFragmentArray = sparseFragmentArray.clone()
        sparseFragmentArray.clear()

        deviceList.forEachIndexed { index, deviceActive ->
            val fragment =
                tempFragmentArray.find { areTheSame(deviceActive.device, it.getDevice()) }
                    ?: DeviceMainFragment.newInstance(deviceActive.device)
            sparseFragmentArray.put(index, fragment)
        }
        submitStatusChangedList()
        notifyDataSetChanged()
    }

    private fun areTheSame(device: Device, otherDevice: Device?): Boolean {
        return otherDevice?.let {
            device.macAddress == it.macAddress && device.name == it.name
        } ?: false
    }

    override fun getItemPosition(element: Any): Int =
        if (element is DeviceMainFragment) {
            sparseFragmentArray.keyOf(element)?.plus(1) ?: PagerAdapter.POSITION_NONE
        } else {
            PagerAdapter.POSITION_UNCHANGED
        }

    override fun getItem(position: Int) =
        if (position == 0) WelcomeFragment.newInstance()
        else {
            Timber.d("$position")
            getDeviceMainFragment(position - 1)
        }

    private fun getDeviceMainFragment(position: Int): DeviceMainFragment =
        sparseFragmentArray[position]
            ?: DeviceMainFragment.newInstance(deviceList[position].device).apply {
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


    private fun submitStatusChangedList() {
        val sparseArray = sparseFragmentArray
        for (i in 0 until sparseArray.size()) {
            sparseArray.get(i)?.let {
                val device = deviceList[i]
                getScanStatusItemOrNull(i)?.setStatus(device.active)
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? = if (position == 0) "Welcome" else ""

    override fun getCount(): Int {
        return deviceList.size + 1
    }
}