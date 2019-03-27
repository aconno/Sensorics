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
            0 -> BeaconGeneral2Fragment.newInstance()
            1 -> BeaconParameter2Fragment.newInstance()
            2 -> BeaconArbitraryDataHtmlFragment.newInstance()
            else -> BeaconSlotHtmlFragment.newInstance(position - 3)
        }
    }

    override fun getCount(): Int {
        return if (beacon == null) 0 else (3 + (beacon?.slots?.size ?: 0))
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