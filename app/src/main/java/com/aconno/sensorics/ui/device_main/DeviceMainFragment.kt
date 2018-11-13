package com.aconno.sensorics.ui.device_main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

class DeviceMainFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        private const val DEVICE_NAME_EXTRA = "device_name"

        private const val MAC_ADDRESS_EXTRA = "mac_address"

        fun newInstance(deviceName: String, macAddress: String): DeviceMainFragment {
            val deviceMainFragment = DeviceMainFragment()
            deviceMainFragment.arguments = Bundle().apply {
                putString(DEVICE_NAME_EXTRA, deviceName)
                putString(MAC_ADDRESS_EXTRA, macAddress)
            }
            return deviceMainFragment
        }
    }
}