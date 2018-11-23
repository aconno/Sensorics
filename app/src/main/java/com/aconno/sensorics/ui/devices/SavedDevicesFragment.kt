package com.aconno.sensorics.ui.devices

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.DeviceActiveAdapter
import com.aconno.sensorics.adapter.DeviceSwipeToDismissHelper
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.Settings
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.IconInfo
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialog
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialogListener
import com.aconno.sensorics.viewmodel.DeviceViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_saved_devices.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class SavedDevicesFragment : DaggerFragment(),
    ScannedDevicesDialogListener,
    DeviceSwipeToDismissHelper.RecyclerItemTouchHelperListener, IconInfo {
    @Inject
    lateinit var deviceViewModel: DeviceViewModel

    @Inject
    lateinit var settings: Settings

    private lateinit var deviceAdapter: DeviceActiveAdapter

    private var listener: SavedDevicesFragmentListener? = null

    private var snackbar: Snackbar? = null

    private var dontObserveQueue: Queue<Boolean> = ArrayDeque<Boolean>()

    private var deletedItems = ArrayDeque<DeviceActive>()

    private lateinit var compositeDisposable: CompositeDisposable

    private val snackbarCallback = object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT
                || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE
                || event == Snackbar.Callback.DISMISS_EVENT_SWIPE
                || event == Snackbar.Callback.DISMISS_EVENT_MANUAL
            ) {
                val deletedItem = deletedItems.poll()

                dontObserveQueue.add(true)
                //delete device from db if undo snackbar timeout.
                deviceViewModel.deleteDevice(deletedItem.device)
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

        compositeDisposable = CompositeDisposable()

        deviceAdapter = DeviceActiveAdapter()

        addDisposable(
            deviceAdapter.getOnConnectClickEvents()
                .subscribe {
                    activity?.let { activity ->
                        (activity as MainActivity).connect(it.device)
                    }
                },
            deviceAdapter.getOnItemClickEvents()
                .subscribe {
                    onItemClick(it)
                },
            deviceAdapter.getOnItemLongClickEvents()
                .subscribe {
                    onLongClick(it)
                }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        activity?.menuInflater?.inflate(R.menu.menu_devices, menu)
        menu?.findItem(R.id.action_start_dashboard)?.isVisible = BuildConfig.FLAVOR == "dev"
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
        Timber.i("View created")

        list_devices.layoutManager = LinearLayoutManager(context)
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

        addDisposable(
            deviceViewModel.getSavedDevicesFlowable()
                .subscribe {
                    if (dontObserveQueue.isEmpty()) {
                        displayPreferredDevices(it)
                    } else {
                        dontObserveQueue.poll()
                    }
                }
        )

        addDisposable(
            deviceViewModel.deviceActiveObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    updateActiveorDeactiveDevices(it)
                }
        )

        button_add_device.setOnClickListener {
            snackbar?.dismiss()
            listener?.onFABClicked()
            Timber.d("Button add device clicked")
            ScannedDevicesDialog().show(activity?.supportFragmentManager, "devices_dialog")
        }
    }

    private fun updateActiveorDeactiveDevices(changedDevices: List<DeviceActive>) {
        if (dontObserveQueue.isEmpty()) {
            deviceAdapter.updateActiveDevices(changedDevices)
        }
    }

    fun addDisposable(vararg disposable: Disposable) {
        compositeDisposable.addAll(*disposable)
    }

    private fun displayPreferredDevices(preferredDevices: List<DeviceActive>?) {
        preferredDevices?.let {
            if (preferredDevices.isEmpty()) {
                empty_view.visibility = View.VISIBLE
                deviceAdapter.setDevices(listOf())
            } else {
                empty_view.visibility = View.INVISIBLE
                deviceAdapter.setDevices(preferredDevices)
                deviceAdapter.setIcons(getIconInfoForActiveDevices(preferredDevices))
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
    private fun onLongClick(param: DeviceActive) {
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

    private fun onItemClick(item: DeviceActive) {
        saveClickedDeviceMacAddress(item.device.macAddress)
        activity?.let {
            val mainActivity = it as MainActivity
            mainActivity.showSensorValues(item.device)
        }
    }

    private fun saveClickedDeviceMacAddress(macAddress: String) {
        addDisposable(
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

            deletedItems.add(deviceAdapter.getDevice(position))
            val name = deletedItems.peek().device.getRealName()

            // remove the item from recycler view
            deviceAdapter.removeItem(position)
            if (deviceAdapter.itemCount == 0) {
                displayPreferredDevices(listOf())
            }

            // showing snack bar with Undo option
            snackbar = Snackbar
                .make(container_fragment, "$name removed!", Snackbar.LENGTH_LONG)
            snackbar?.setAction("UNDO") {
                //Prevent it to be removed from snackbarCallback
                snackbar?.removeCallback(snackbarCallback)

                // undo is selected, restore the deleted item
                val lastDevice = deletedItems.last
                deviceAdapter.restoreItem(lastDevice, position)
                deletedItems.remove(lastDevice)

                if (position == 0) {
                    empty_view.visibility = View.INVISIBLE
                }
            }

            snackbar?.addCallback(snackbarCallback)
            snackbar?.setActionTextColor(Color.YELLOW)
            snackbar?.show()
        }
    }

    override fun onDetach() {
        listener = null
        compositeDisposable.dispose()
        snackbar?.removeCallback(snackbarCallback)
        super.onDetach()
    }


    override fun getIconInfoForActiveDevices(deviceNames: List<DeviceActive>): HashMap<String, String> {

        val hashMap: HashMap<String, String> = hashMapOf()

        deviceNames.forEach { device ->
            if (!hashMap.containsKey(device.device.name))
                deviceViewModel.getIconPath(device.device.name)?.let {
                    hashMap[device.device.name] = it
                }
        }
        return hashMap
    }

    override fun getIconInfoForDevices(deviceNames: List<Device>): HashMap<String, String> {
        //This method is not used.
        return hashMapOf()
    }
}