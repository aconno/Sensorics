package com.aconno.sensorics.ui.settings_framework

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.aconno.sensorics.ui.settings_framework.fragments.*

class BeaconSettingsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private var initialData = 0
    var slotCount = 0
        set(value) {
            initialData = 4
            field = value
        }

    fun clear(){
        slotCount=0;
        initialData = 0
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BeaconSettingsGeneralFragment.newInstance()
            1 -> BeaconSettingsParametersFragment.newInstance(position)
            2 -> BeaconSettingsCacheableParamsFragment.newInstance()
            3 -> BeaconSettingsArbitraryDataHtmlFragment.newInstance()
            else -> BeaconSettingsSlotFragment.newInstance(position - 4)
        }
    }

    override fun getCount(): Int {
        return initialData + slotCount
    }


    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "General"
            1 -> "Parameters"
            2 -> "Cacheable parameters"
            3 -> "Arbitrary Data"
            else -> "Slot " + (position - 3).toString()
        }
    }
}