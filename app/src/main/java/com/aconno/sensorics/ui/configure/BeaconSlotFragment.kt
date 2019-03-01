package com.aconno.sensorics.ui.configure

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.SeekBar
import com.aconno.bluetooth.beacon.Slot
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.card_base_params.view.*
import kotlinx.android.synthetic.main.card_frame_type.view.*
import kotlinx.android.synthetic.main.card_trigger.view.*
import timber.log.Timber

class BeaconSlotFragment : Fragment(), SeekBar.OnSeekBarChangeListener,
    AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private val beaconViewModel: BeaconViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconViewModel::class.java)
    }

    private val types: Array<Slot.Type> by lazy {
        beaconViewModel.beacon.value?.supportedSlots ?: arrayOf(Slot.Type.EMPTY)
    }
    private val triggerTypes: Array<Slot.TriggerType> = Slot.TriggerType.values()
    private lateinit var slot: Slot
    private var slotPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuration_slot, container, false)
        @Suppress("UNCHECKED_CAST")
        arguments?.let { it ->
            slotPosition = it.getInt(EXTRA_BEACON_SLOT_POSITION)
            val tmp = beaconViewModel.beacon.value?.slots?.get(slotPosition)
            if (tmp == null) slot = Slot(Slot.Type.EMPTY).also {
                beaconViewModel.beacon.value!!.slots[slotPosition] = it
            } else {
                slot = tmp
            }

            view.spinner_frame_type.adapter = FrameTypeAdapter(context!!, types)
            Timber.e("${types.indexOf(slot.type)}")
            view.spinner_frame_type.setSelection(types.indexOf(slot.type), false)
            Timber.e("${types.indexOf(slot.type)}")
            view.spinner_frame_type.onItemSelectedListener = this

//            view.sb_advertising_interval.min = 0
            view.sb_advertising_interval.max = 100
            view.sb_advertising_interval.progress = slot.advertisingInterval / 100
            view.sb_advertising_interval.setOnSeekBarChangeListener(this)

//            view.sb_rssi_1m.min = 0
            view.sb_rssi_1m.max = 120
            view.sb_rssi_1m.progress = slot.rssi1m
            view.sb_rssi_1m.setOnSeekBarChangeListener(this)

//            view.sb_radio_tx.min = 0A
            view.sb_radio_tx.max = 120
            view.sb_radio_tx.progress = slot.radioTx
            view.sb_radio_tx.setOnSeekBarChangeListener(this)

            view.switch_trigger_enable.isChecked = slot.triggerEnabled
            view.switch_trigger_enable.setOnCheckedChangeListener(this)

            view.spinner_trigger_type.adapter = TriggerTypeAdapter(context!!, triggerTypes)
            view.spinner_trigger_type.setSelection(triggerTypes.indexOf(slot.triggerType), false)
            view.spinner_trigger_type.onItemSelectedListener = this

            updateAdvertisementFragment(types.indexOf(slot.type))
            view.tv_advertising_interval_value.text =
                getString(R.string.ms_format, view.sb_advertising_interval.progress * 10)
            view.tv_rssi_1m_value.text =
                getString(R.string.dbm_format, view.sb_rssi_1m.progress - 100)
            view.tv_radio_tx_value.text =
                getString(R.string.dbm_format, view.sb_radio_tx.progress - 100)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        view?.let { view ->
            when (seekBar) {
                view.sb_advertising_interval -> {
                    view.tv_advertising_interval_value.text =
                        getString(R.string.ms_format, progress * 10)
                    slot.advertisingInterval = progress * 10
                }
                view.sb_rssi_1m -> {
                    view.tv_rssi_1m_value.text = getString(R.string.dbm_format, progress - 100)
                    slot.rssi1m = progress - 100
                }
                view.sb_radio_tx -> {
                    view.tv_radio_tx_value.text = getString(R.string.dbm_format, progress - 100)
                    slot.radioTx = progress - 100
                }
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        view?.let {
            when (buttonView) {
                it.switch_trigger_enable -> slot.triggerEnabled = isChecked
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.view?.let {
            when (parent) {
                it.spinner_trigger_type -> slot.triggerType = triggerTypes[position]
                it.spinner_frame_type -> {
                    // TODO: BIG BUG IF THIS PAGE ALREADY EXISTED
                    Timber.e("Changing type to ${types[position]}")
                    slot.type = types[position]
                    updateAdvertisementFragment(position)
                }
                else -> 0
            }
        }
    }

    private fun updateAdvertisementFragment(position: Int): Int {
        return childFragmentManager.beginTransaction().apply {
            if (types[position].hasAdvertisingContent) {
                this.replace(
                    R.id.fl_advertising_content,
                    AdvertisingContentFragment.newInstance(slotPosition),
                    FRAGMENT_TAG_SLOT
                )
            } else {
                childFragmentManager.findFragmentByTag(FRAGMENT_TAG_SLOT)?.let {
                    this.remove(it)
                }
            }
        }.commit()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    companion object {
        const val FRAGMENT_TAG_SLOT = "com.aconno.beaconapp.FRAGMENT_SLOT"
        const val EXTRA_BEACON_SLOT_POSITION = "com.aconno.beaconapp.BEACON_SLOT_POSITION"

        @JvmStatic
        fun newInstance(slotPosition: Int) =
            BeaconSlotFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_BEACON_SLOT_POSITION, slotPosition)
                }
            }
    }
}
