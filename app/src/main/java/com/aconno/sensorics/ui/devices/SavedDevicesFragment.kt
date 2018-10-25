package com.aconno.sensorics.ui.devices

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.EditText
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.DeviceActiveAdapter
import com.aconno.sensorics.adapter.DeviceSwipeToDismissHelper
import com.aconno.sensorics.adapter.ItemClickListener
import com.aconno.sensorics.adapter.LongItemClickListener
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.Settings
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialog
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialogListener
import com.aconno.sensorics.viewmodel.DeviceViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_saved_devices.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class SavedDevicesFragment : DaggerFragment(), ItemClickListener<DeviceActive>,
    ScannedDevicesDialogListener,
    LongItemClickListener<DeviceActive>,
    DeviceSwipeToDismissHelper.RecyclerItemTouchHelperListener {

    @Inject
    lateinit var deviceViewModel: DeviceViewModel

    @Inject
    lateinit var settings: Settings

    private lateinit var deviceAdapter: DeviceActiveAdapter

    private lateinit var listener: SavedDevicesFragmentListener

    private var devices: MutableList<DeviceActive> = mutableListOf()

    private var dontObserveQueue: Queue<Boolean> = ArrayDeque<Boolean>()

    private var snackbar: Snackbar? = null

    private val disposables = CompositeDisposable()

    private val onConnectClickListener = object : ItemClickListener<DeviceActive> {
        override fun onItemClick(item: DeviceActive) {
            activity?.let {
                val mainActivity = it as MainActivity
                mainActivity.connect(item.device)
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
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        activity?.menuInflater?.inflate(R.menu.menu_devices, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        context?.let { context ->
            when (item.itemId) {
                R.id.action_start_actions_activity -> {
                    if (deviceAdapter.itemCount > 0) {
                        ActionListActivity.start(context)
                    } else {
                        Snackbar.make(
                            container_fragment,
                            R.string.message_no_saved_devices_cannot_open_actions,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    return true
                }
                R.id.action_start_dashboard -> {
                    (activity as MainActivity).onDashboardClicked()
                    return true
                }
                else -> {
                    //Do nothing
                }
            }
        }
        return super.onOptionsItemSelected(item)
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
        deviceAdapter = DeviceActiveAdapter(devices, this, onConnectClickListener, this)
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
            if (it != null) {
                if (!(dontObserveQueue.size > 0 && dontObserveQueue.poll()) || it.isEmpty()) {
                    displayPreferredDevices(it)
                }
            }
        })

        button_add_device.setOnClickListener {
            snackbar?.dismiss()
            listener.onFABClicked()
            Timber.d("Button add device clicked")
            ScannedDevicesDialog().show(activity?.supportFragmentManager, "devices_dialog")
        }
    }

    private fun displayPreferredDevices(preferredDevices: List<DeviceActive>?) {
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
        mainActivity?.supportActionBar?.title = getString(R.string.title_device_list)
        mainActivity?.supportActionBar?.subtitle = ""
    }

    @SuppressLint("InflateParams")
    override fun onLongClick(param: DeviceActive) {
        val builder = AlertDialog.Builder(context)

        val inflate = layoutInflater.inflate(R.layout.layout_rename, null)
        val input = inflate.findViewById<EditText>(R.id.edit_name)
        input.setText(param.device.getRealName())

        val dialogClickListener: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val text = input.text.toString()

                        if (!text.isBlank()) {
                            deviceViewModel.updateDevice(param.device, text)
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

    fun onBluetoothOn() {
        button_add_device.show()
    }

    fun onBluetoothOff() {
        button_add_device.hide()
    }

    override fun onDevicesDialogItemClick(item: Device) {
        deviceViewModel.saveDevice(item)
    }

    override fun onItemClick(item: DeviceActive) {
        saveClickedDeviceMacAddress(item.device.macAddress)
        activity?.let {
            val mainActivity = it as MainActivity
            mainActivity.showSensorValues(item.device)
        }
    }

    private fun saveClickedDeviceMacAddress(macAddress: String) {
        disposables.add(
            settings.setClickedDeviceMac(macAddress)
                .subscribe(
                    {
                        Timber.d("Mac address saved for clicked device, mac: $macAddress")
                    },
                    { throwable ->
                        Timber.d(throwable)
                    }
                )
        )
    }

    override fun onDialogDismissed() {
        //Do-Nothing
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is DeviceActiveAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar and backup for undo

            val deletedItem = devices[position]
            val name = deletedItem.device.getRealName()

            // remove the item from recycler view
            deviceAdapter.removeItem(position)

            // showing snack bar with Undo option
            snackbar = Snackbar
                .make(container_fragment, "$name removed!", Snackbar.LENGTH_LONG)
            snackbar?.setAction("UNDO") {
                // undo is selected, restore the deleted item
                deviceAdapter.restoreItem(deletedItem, position)
            }

            snackbar?.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT
                        || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE
                        || event == Snackbar.Callback.DISMISS_EVENT_SWIPE
                        || event == Snackbar.Callback.DISMISS_EVENT_MANUAL
                    ) {
                        //delete device from db if undo snackbar timeout.
                        deviceViewModel.deleteDevice(deletedItem.device)
                        dontObserveQueue.add(true)
                    }
                }
            })
            snackbar?.setActionTextColor(Color.YELLOW)
            snackbar?.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}