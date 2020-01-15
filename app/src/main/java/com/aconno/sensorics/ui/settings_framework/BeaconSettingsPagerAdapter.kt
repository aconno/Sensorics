package com.aconno.sensorics.ui.settings_framework

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

class BeaconSettingsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    var slotCount = 0

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BeaconSettingsGeneralFragment.newInstance()
            1 -> BeaconSettingsParametersFragment.newInstance() // TODO: missing cacheable param fragment
            2 -> BeaconSettingsArbitraryDataHtmlFragment.newInstance()
            else -> BeaconSettingsSlotFragment.newInstance(position - 3)
        }
    }

    override fun getCount(): Int {
        return 3 + slotCount // TODO: missing cacheable param fragment
    }


    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "General"
            1 -> "Parameters" // TODO: missing cacheable param fragment
            2 -> "Arbitrary Data"
            else -> "Slot " + (position - 2).toString()
        }
    }
}