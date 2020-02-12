package com.aconno.sensorics.ui.beacon_settings

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.aconno.sensorics.ui.beacon_settings.fragments.BeaconSettingsArbitraryDataFragment
import com.aconno.sensorics.ui.beacon_settings.fragments.BeaconSettingsGeneralFragment
import com.aconno.sensorics.ui.beacon_settings.fragments.BeaconSettingsParametersFragment
import com.aconno.sensorics.ui.beacon_settings.fragments.BeaconSettingsSlotFragment

const val NON_SLOTS_NUMBER_COUNT = 3

class BeaconSettingsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private var initialData = 0
    var slotCount = 0
        set(value) {
            initialData = NON_SLOTS_NUMBER_COUNT
            field = value
        }

    fun clear() {
        slotCount = 0;
        initialData = 0
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BeaconSettingsGeneralFragment.newInstance()
            1 -> BeaconSettingsParametersFragment.newInstance()
            2 -> BeaconSettingsArbitraryDataFragment.newInstance()
            else -> BeaconSettingsSlotFragment.newInstance(position - NON_SLOTS_NUMBER_COUNT)
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
            2 -> "Arbitrary Data"
            else -> "Slot " + (position - NON_SLOTS_NUMBER_COUNT + 1).toString()
        }
    }
}