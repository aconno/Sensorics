package com.aconno.acnsensa.ui.devices

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.DeviceAdapter
import com.aconno.acnsensa.adapter.ItemClickListener
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.ui.MainActivity
import com.aconno.acnsensa.ui.dialogs.ScannedDevicesDialog
import com.aconno.acnsensa.ui.dialogs.ScannedDevicesDialogListener
import com.aconno.acnsensa.viewmodel.DeviceViewModel
import kotlinx.android.synthetic.main.fragment_saved_devices.*
import timber.log.Timber
import javax.inject.Inject

class SavedDevicesFragment : Fragment(), ItemClickListener<Device>, ScannedDevicesDialogListener {

    @Inject
    lateinit var deviceViewModel: DeviceViewModel

    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivity: MainActivity? = activity as MainActivity
        mainActivity?.mainActivityComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved_devices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_devices.layoutManager = LinearLayoutManager(context)
        deviceAdapter = DeviceAdapter(mutableListOf(), this)
        list_devices.adapter = deviceAdapter

        deviceViewModel.getSavedDevicesLiveData().observe(this, Observer {
            displayPreferredDevices(it)
        })

        button_add_device.setOnClickListener {
            Timber.d("Button add device clicked")
            ScannedDevicesDialog().show(activity?.supportFragmentManager, "devices_dialog")
        }
    }

    private fun displayPreferredDevices(preferredDevices: List<Device>?) {
        preferredDevices?.let {
            if (preferredDevices.isEmpty()) {
                empty_view.visibility = View.VISIBLE
                deviceAdapter.clearDevices()
            } else {
                empty_view.visibility = View.INVISIBLE
                deviceAdapter.setDevices(preferredDevices)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity? = context as MainActivity
        mainActivity?.supportActionBar?.title = "Devices"
    }

    override fun onDevicesDialogItemClick(item: Device) {
        deviceViewModel.saveDevice(item)
    }

    override fun onItemClick(item: Device) {
        activity?.let {
            val mainActivity = it as MainActivity
            mainActivity.showSensorValues(item.macAddress)
        }
    }
}