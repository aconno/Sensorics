package com.aconno.sensorics.ui.configure

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.bluetooth.beacon.Beacon
import com.aconno.bluetooth.beacon.Slot
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.card_beacon_configuration.view.*
import kotlinx.android.synthetic.main.card_feature_info_configuration.view.*
import kotlinx.android.synthetic.main.card_manufacturer_info_configuration.view.*


class BeaconGeneralFragment : Fragment() {
    private var listener: OnBeaconGeneralFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuration_general, container, false)
        @Suppress("UNCHECKED_CAST")
        arguments?.let {
            view.switch_connectible.isChecked = it.getBoolean(EXTRA_BEACON_CONNECTIBLE)
            view.tv_manufacturer.text = it.getString(EXTRA_BEACON_MANUFACTURER)
            view.tv_model.text = it.getString(EXTRA_BEACON_MODEL)
            view.tv_sw_version.text = it.getString(EXTRA_BEACON_SW_VERSION)
            view.tv_hw_version.text = it.getString(EXTRA_BEACON_HW_VERSION)
            view.tv_fw_version.text = it.getString(EXTRA_BEACON_FW_VERSION)
            view.tv_address.text = it.getString(EXTRA_BEACON_ADDRESS)
            view.tv_supported_tx_power.text =
                (it.getSerializable(EXTRA_BEACON_SUPPORTED_TX_POWER) as Array<Int>).joinToString(", ")
            view.tv_supported_slots.text =
                (it.getSerializable(EXTRA_BEACON_SUPPORTED_SLOTS) as Array<Slot.Type>).joinToString(
                    ", "
                ) { it.name }
            view.tv_adv_feature.text = it.getString(EXTRA_BEACON_ADV_FEATURE)
            view.tv_slot_amt.text = it.getInt(EXTRA_BEACON_SLOT_AMOUNT).toString()

            view.switch_connectible.setOnCheckedChangeListener { _, isChecked ->
                listener?.onDataUpdated(Bundle().apply {
                    putBoolean(EXTRA_BEACON_CONNECTIBLE, isChecked)
                })
            }
            view.cv_button_update_firmware.setOnClickListener { listener?.updateFirmware() }
            view.cv_button_reset_factory.setOnClickListener { listener?.resetFactory() }
            view.cv_button_power_off.setOnClickListener { listener?.powerOff() }
            view.cv_button_add_password.setOnClickListener { listener?.addPassword() }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBeaconGeneralFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnBeaconGeneralFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnBeaconGeneralFragmentInteractionListener {
        fun onDataUpdated(bundle: Bundle)
        fun updateFirmware()
        fun resetFactory()
        fun powerOff()
        fun addPassword()
    }

    companion object {
        const val EXTRA_BEACON_CONNECTIBLE: String = "com.aconno.beaconapp.BEACON_CONNECTIBLE"
        const val EXTRA_BEACON_MANUFACTURER: String = "com.aconno.beaconapp.BEACON_MANUFACTURER"
        const val EXTRA_BEACON_MODEL: String = "com.aconno.beaconapp.BEACON_MODEL"
        const val EXTRA_BEACON_SW_VERSION: String = "com.aconno.beaconapp.BEACON_SW_VERSION"
        const val EXTRA_BEACON_HW_VERSION: String = "com.aconno.beaconapp.BEACON_HW_VERSION"
        const val EXTRA_BEACON_FW_VERSION: String = "com.aconno.beaconapp.BEACON_FW_VERSION"
        const val EXTRA_BEACON_ADDRESS: String = "com.aconno.beaconapp.BEACON_ADDRESS"
        const val EXTRA_BEACON_SUPPORTED_TX_POWER: String =
            "com.aconno.beaconapp.BEACON_SUPPORTED_TX_POWER"
        const val EXTRA_BEACON_SUPPORTED_SLOTS: String =
            "com.aconno.beaconapp.BEACON_SUPPORTED_SLOTS"
        const val EXTRA_BEACON_ADV_FEATURE: String = "com.aconno.beaconapp.BEACON_ADV_FEATURE"
        const val EXTRA_BEACON_SLOT_AMOUNT: String = "com.aconno.beaconapp.BEACON_SLOT_AMOUNT"

        @JvmStatic
        fun newInstance(beacon: Beacon) =
            BeaconGeneralFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_BEACON_CONNECTIBLE, beacon.connectible)
                    putString(EXTRA_BEACON_MANUFACTURER, beacon.manufacturer)
                    putString(EXTRA_BEACON_MODEL, beacon.model)
                    putString(EXTRA_BEACON_SW_VERSION, beacon.softwareVersion)
                    putString(EXTRA_BEACON_HW_VERSION, beacon.hardwareVersion)
                    putString(EXTRA_BEACON_FW_VERSION, beacon.firmwareVersion)
                    putString(EXTRA_BEACON_ADDRESS, beacon.address)
                    putSerializable(EXTRA_BEACON_SUPPORTED_TX_POWER, beacon.supportedTxPower)
                    putSerializable(EXTRA_BEACON_SUPPORTED_SLOTS, beacon.supportedSlots)
                    putString(EXTRA_BEACON_ADV_FEATURE, beacon.advFeature)
                    putInt(EXTRA_BEACON_SLOT_AMOUNT, beacon.slotAmount)
                }
            }
    }
}
