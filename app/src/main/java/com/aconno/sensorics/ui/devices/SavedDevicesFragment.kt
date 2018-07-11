package com.aconno.sensorics.ui.devices

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.DeviceAdapter
import com.aconno.sensorics.adapter.DeviceSwipeToDismissHelper
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
    LongItemClickListener<Device>, DeviceSwipeToDismissHelper.RecyclerItemTouchHelperListener {

    @Inject
    lateinit var deviceViewModel: DeviceViewModel

    private lateinit var deviceAdapter: DeviceAdapter

    private lateinit var listener: SavedDevicesFragmentListener

    private var devices: MutableList<Device> = mutableListOf()

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
        deviceAdapter = DeviceAdapter(devices, this, this)
        list_devices.adapter = deviceAdapter

        list_devices.itemAnimator = DefaultItemAnimator()
        list_devices.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        val itemTouchHelperCallback =
            DeviceSwipeToDismissHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(list_devices)

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

    @SuppressLint("InflateParams")
    override fun onLongClick(param: Device) {
        val builder = AlertDialog.Builder(context)

        val inflate = layoutInflater.inflate(R.layout.layout_rename, null)
        val input = inflate.findViewById<EditText>(R.id.edit_name)
        input.setText(if (param.alias.isBlank()) param.name else param.alias)

        val dialogClickListener: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val text = input.text.toString()

                        if (!text.isBlank()) {
                            val updateDevice = deviceViewModel.updateDevice(param, text)
                            val index = devices.indexOf(param)
                            devices[index] = updateDevice
                            deviceAdapter.notifyItemChanged(index)
                        }

                        dialog.dismiss()
                    }

                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.dismiss()
                    }
                }
            }

        builder
            .setView(inflate)
            .setTitle("Rename Beacon")
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

    override fun onDialogDismissed() {
        //Do-Nothing
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        if (viewHolder is DeviceAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar and backup for undo
            val deletedItem = devices[viewHolder.getAdapterPosition()]
            val name = if (deletedItem.alias.isEmpty()) deletedItem.name else deletedItem.alias
            val deletedIndex = viewHolder.getAdapterPosition()

            // remove the item from recycler view
            deviceAdapter.removeItem(viewHolder.getAdapterPosition())

            // showing snack bar with Undo option
            val snackbar = Snackbar
                .make(coordinatorLayout, "$name removed!", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO") {
                // undo is selected, restore the deleted item
                deviceAdapter.restoreItem(deletedItem, deletedIndex)
            }

            snackbar.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        //delete device from db if undo snackbar timeout.
                        deviceViewModel.deleteDevice(deletedItem)
                    }
                }
            })
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
        }
    }
}