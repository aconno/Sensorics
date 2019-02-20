package com.aconno.sensorics.ui.configure

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import com.aconno.bluetooth.beacon.Slot
import com.aconno.bluetooth.beacon.Slot.Companion.EXTRA_BEACON_SLOT_POSITION
import com.aconno.bluetooth.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM
import com.aconno.bluetooth.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR
import com.aconno.bluetooth.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_IBEACON_MINOR
import com.aconno.bluetooth.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_IBEACON_UUID
import com.aconno.bluetooth.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID
import com.aconno.bluetooth.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID
import com.aconno.bluetooth.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_URL_URL
import com.aconno.bluetooth.beacon.hexStringToByteArray
import com.aconno.bluetooth.beacon.toCompactHex
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.input_table_adv_content_custom.*
import kotlinx.android.synthetic.main.input_table_adv_content_custom.view.*
import kotlinx.android.synthetic.main.input_table_adv_content_ibeacon.view.*
import kotlinx.android.synthetic.main.input_table_adv_content_uid.view.*
import kotlinx.android.synthetic.main.input_table_adv_content_url.view.*
import java.util.*

class AdvertisingContentFragment : Fragment(), TextWatcher, CompoundButton.OnCheckedChangeListener {

    private val beaconViewModel: BeaconViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconViewModel::class.java)
    }
    private lateinit var slot: Slot

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            slot =
                beaconViewModel.beacon.value!!.slots[it.getInt(BeaconSlotFragment.EXTRA_BEACON_SLOT_POSITION)]!!
        }
        return inflater.inflate(
            when (slot.type) {
                Slot.Type.UID -> R.layout.input_table_adv_content_uid
                Slot.Type.URL -> R.layout.input_table_adv_content_url
                Slot.Type.I_BEACON -> R.layout.input_table_adv_content_ibeacon
                Slot.Type.CUSTOM -> R.layout.input_table_adv_content_custom
                else -> TODO()
            }, container, false
        ).apply {
            when (slot.type) {
                Slot.Type.UID -> {
                    this.et_namespace_id.setText(
                        slot.slotAdvertisingContent.getOrElse(
                            KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID
                        ) { "" }.toString()
                    )
                    this.et_namespace_id.addTextChangedListener(this@AdvertisingContentFragment)
                    this.et_instance_id.setText(
                        slot.slotAdvertisingContent.getOrElse(
                            KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID
                        ) { "" }.toString()
                    )
                    this.et_instance_id.addTextChangedListener(this@AdvertisingContentFragment)
                }
                Slot.Type.URL -> {
                    this.et_url.setText(
                        slot.slotAdvertisingContent.getOrElse(
                            KEY_ADVERTISING_CONTENT_URL_URL
                        ) { "" }.toString()
                    )
                    this.et_url.addTextChangedListener(this@AdvertisingContentFragment)
                }
                Slot.Type.I_BEACON -> {
                    this.et_uuid.setText(
                        slot.slotAdvertisingContent.getOrElse(
                            KEY_ADVERTISING_CONTENT_IBEACON_UUID
                        ) { "" }.toString()
                    )
                    this.et_uuid.addTextChangedListener(this@AdvertisingContentFragment)
                    this.et_major.setText(
                        slot.slotAdvertisingContent.getOrElse(
                            KEY_ADVERTISING_CONTENT_IBEACON_MAJOR
                        ) { "" }.toString()
                    )
                    this.et_major.addTextChangedListener(this@AdvertisingContentFragment)
                    this.et_minor.setText(
                        slot.slotAdvertisingContent.getOrElse(
                            KEY_ADVERTISING_CONTENT_IBEACON_MINOR
                        ) { "" }.toString()
                    )
                    this.et_minor.addTextChangedListener(this@AdvertisingContentFragment)
                }
                Slot.Type.CUSTOM -> {
                    this.et_custom.setText(
                        slot.slotAdvertisingContent.getOrElse(
                            KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM
                        ) { "" }.toString()
                    )
                    this.et_custom.addTextChangedListener(this@AdvertisingContentFragment)
                    this.sw_hex_mode.setOnCheckedChangeListener(this@AdvertisingContentFragment)
                }
                else -> TODO()
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        buttonView?.let { checkedView ->
            view?.let {
                when (checkedView.id) {
                    it.sw_hex_mode.id -> {
                        if (isChecked) {
                            this.et_custom.setText(
                                this.et_custom.text.toString().toByteArray(
                                    ENCODING_FOR_CUSTOM
                                ).toCompactHex()
                            )
                        } else {
                            this.et_custom.setText(
                                this.et_custom.text.toString().hexStringToByteArray().toString(
                                    ENCODING_FOR_CUSTOM
                                )
                            )
                        }
                        slot.slotAdvertisingContent.put(
                            KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM,
                            if (isChecked) this.et_custom.text.toString().hexStringToByteArray().toString(
                                ENCODING_FOR_CUSTOM
                            ) else this.et_custom.text.toString()
                        )
                    }
                    else -> TODO()
                }
            }
        }
    }


    override fun afterTextChanged(editable: Editable?) {
        activity?.currentFocus?.let { textView ->
            view?.let {
                when (slot.type) {
                    Slot.Type.UID -> {
                        when (textView.id) {
                            it.et_namespace_id.id -> slot.slotAdvertisingContent[KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID] =
                                editable.toString()
                            it.et_instance_id.id -> slot.slotAdvertisingContent[KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID] =
                                editable.toString()
                            else -> throw IllegalStateException("Other EditTexts should not exist on the UID Slot Type")
                        }
                    }
                    Slot.Type.URL -> {
                        when (textView.id) {
                            it.et_url.id -> slot.slotAdvertisingContent[KEY_ADVERTISING_CONTENT_URL_URL] =
                                editable.toString()
                            else -> throw IllegalStateException("Other EditTexts should not exist on the URL Slot Type")
                        }
                    }
                    Slot.Type.I_BEACON -> {
                        when (textView.id) {
                            it.et_uuid.id -> slot.slotAdvertisingContent[KEY_ADVERTISING_CONTENT_IBEACON_UUID] =
                                try {
                                    val uuid: UUID =
                                        UUID.fromString(editable.toString()) // TODO: Validity to TextInputLayout
                                    Toast.makeText(context, "UUID is valid.", Toast.LENGTH_SHORT)
                                        .show()
                                    uuid
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Invalid UUID, zeroed UUID will be used.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    UUID.fromString("00000000-0000-0000-0000-000000000000")

                                }
                            it.et_major.id -> slot.slotAdvertisingContent[KEY_ADVERTISING_CONTENT_IBEACON_MAJOR] =
                                editable.toString().toIntOrNull() ?: 0
                            it.et_minor.id -> slot.slotAdvertisingContent[KEY_ADVERTISING_CONTENT_IBEACON_MINOR] =
                                editable.toString().toIntOrNull() ?: 0
                            else -> throw IllegalStateException("Other EditTexts should not exist on the iBeacon Slot Type")
                        }
                    }
                    Slot.Type.CUSTOM -> {
                        when (textView.id) {
                            it.et_custom.id -> slot.slotAdvertisingContent.put(
                                KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM,
                                if (it.sw_hex_mode.isChecked) this.et_custom.text.toString().hexStringToByteArray().toString(
                                    ENCODING_FOR_CUSTOM
                                ) else this.et_custom.text.toString()
                            )
                            else -> throw IllegalStateException("Other EditTexts should not exist on the Custom Slot Type")
                        }
                    }
                    else -> TODO()
                }

            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    companion object {

        @JvmStatic
        fun newInstance(slotPosition: Int) =
            AdvertisingContentFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_BEACON_SLOT_POSITION, slotPosition)
                }
            }
    }

    // TODO: Display invalid ascii characters as \x00 (or whatever hex representation)
    private val ENCODING_FOR_CUSTOM = charset("UTF-8")
}

