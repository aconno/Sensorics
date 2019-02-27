package com.aconno.sensorics.ui.configure

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.aconno.bluetooth.beacon.Beacon

class BeaconPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    var beacon: Beacon? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            //0 -> BeaconGeneralFragment.newInstance(beacon!!)
            0 -> BeaconGeneral2Fragment.newInstance(beacon!!)
            1 -> BeaconParameterFragment.newInstance()
            2 -> BeaconArbitraryDataFragment.newInstance()
            3 -> BeaconGeneralFragment.newInstance(beacon!!)
            else -> BeaconSlotFragment.newInstance(position - 3)
        }
    }

    override fun getCount(): Int {
        return if (beacon == null) 0 else (3 + (beacon?.slotAmount ?: 0))
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "General"
            1 -> "Parameters"
            2 -> "Arbitrary Data"
            else -> "Slot " + (position - 2).toString()
        }
    }
}