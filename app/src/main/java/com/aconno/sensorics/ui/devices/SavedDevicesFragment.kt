package com.aconno.sensorics.ui.devices

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.DeviceAdapter
import com.aconno.sensorics.adapter.ItemClickListener
import com.aconno.sensorics.adapter.LongItemClickListener
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialog
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialogListener
import com.aconno.sensorics.viewmodel.DeviceViewModel
import kotlinx.android.synthetic.main.fragment_saved_devices.*
import timber.log.Timber
import javax.inject.Inject

class SavedDevicesFragment : Fragment(), ItemClickListener<Device>, ScannedDevicesDialogListener,
    LongItemClickListener<Device> {

    @Inject
    lateinit var deviceViewModel: DeviceViewModel

    private lateinit var deviceAdapter: DeviceAdapter

    private var selectedItem: Device? = null

    private lateinit var listener: SavedDevicesFragmentListener

    var dialogClickListener: DialogInterface.OnClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    selectedItem?.let {
                        deviceViewModel.deleteDevice(it)
                        deviceAdapter.deleteDevice(it)
                    }
                    dialog.dismiss()
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                }
            }
        }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is SavedDevicesFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ${SavedDevicesFragmentListener::class}")
        }
    }

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
        deviceAdapter = DeviceAdapter(mutableListOf(), this, this)
        list_devices.adapter = deviceAdapter

        deviceViewModel.getSavedDevicesLiveData().observe(this, Observer {
            displayPreferredDevices(it)
        })

        button_add_device.setOnClickListener {
            listener.onFABClicked()
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

    override fun onLongClick(param: Device) {
        selectedItem = param
        val builder = AlertDialog.Builder(context)

        builder.setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener)
            .show()
    }

    override fun onDevicesDialogItemClick(item: Device) {
        deviceViewModel.saveDevice(item)
    }

    override fun onItemClick(item: Device) {
        activity?.let {
            val mainActivity = it as MainActivity
            mainActivity.showSensorValues(item)
        }
    }
}