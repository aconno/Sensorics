package com.aconno.sensorics.ui.settings_framework

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.ui.configure.BeaconArbitraryDataHtmlFragment
import com.aconno.sensorics.ui.configure.BeaconParameter2Fragment
import com.aconno.sensorics.ui.configure.BeaconSlotHtmlFragment

class BeaconSettingsPagerAdapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        var beacon: Beacon? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> BeaconSettingsGeneralFragment.newInstance()
                1 -> BeaconSettingsParametersFragment.newInstance()
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