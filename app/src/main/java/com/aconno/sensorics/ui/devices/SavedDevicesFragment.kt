package com.aconno.sensorics.ui.devices

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.recyclerview.widget.*
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.DeviceActiveAdapter
import com.aconno.sensorics.adapter.DeviceSwipeToDismissHelper
import com.aconno.sensorics.adapter.SelectableRecyclerViewAdapter
import com.aconno.sensorics.domain.interactor.ifttt.action.SetActionActiveByDeviceMacAddressUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.DeviceGroup
import com.aconno.sensorics.domain.repository.Settings
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.DeviceActive
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.IconInfo
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialog
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialogListener
import com.aconno.sensorics.viewmodel.DeviceGroupViewModel
import com.aconno.sensorics.viewmodel.DeviceViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_create_group.view.*
import kotlinx.android.synthetic.main.fragment_saved_devices.*
import timber.log.Timber
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject


class SavedDevicesFragment : DaggerFragment(),
    ScannedDevicesDialogListener,
    DeviceSwipeToDismissHelper.RecyclerItemTouchHelperListener, IconInfo,
    SelectableRecyclerViewAdapter.ItemClickListener<DeviceActive>,
    SelectableRecyclerViewAdapter.ItemLongClickListener<DeviceActive>,
    SelectableRecyclerViewAdapter.ItemSelectedListener<DeviceActive>
{
    @Inject
    lateinit var deviceViewModel: DeviceViewModel

    @Inject
    lateinit var deviceGroupViewModel: DeviceGroupViewModel

    lateinit var deviceGroupsTabs : DeviceGroupTabs

    private var deviceGroupOptions = DeviceGroupOptions()

    @Inject
    lateinit var settings: Settings

    @Inject
    lateinit var setActionActiveByDeviceMacAddressUseCase: SetActionActiveByDeviceMacAddressUseCase

    private lateinit var deviceAdapter: DeviceActiveAdapter

    private var listener: SavedDevicesFragmentListener? = null

    private var snackbar: Snackbar? = null

    private var dontObserveQueue: Queue<Boolean> = ArrayDeque<Boolean>()

    private var deletedItems = ArrayDeque<DeviceActive>()

    private lateinit var compositeDisposable: CompositeDisposable

    private var selectionStateListener: ItemSelectionStateListener? = null

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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is SavedDevicesFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ${SavedDevicesFragmentListener::class}")
        }
        if (context is ItemSelectionStateListener) {
            selectionStateListener = context
        }

        compositeDisposable = CompositeDisposable()

        deviceAdapter = DeviceActiveAdapter(this,this,this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        if(deviceAdapter.isItemSelectionEnabled) {
            inflater.inflate(R.menu.menu_selected_devices, menu)
            menu.findItem(R.id.action_remove_devices_from_group)?.isVisible = deviceGroupsTabs.isDeviceGroupTabActive()
        } else {
            inflater.inflate(R.menu.menu_devices, menu)
        }

        menu.findItem(R.id.action_start_dashboard)?.isVisible = BuildConfig.FLAVOR == "dev"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        context?.let { context ->
            when (item.itemId) {
                android.R.id.home -> {
                    exitItemSelectionState()
                    return true
                }
                R.id.action_select_all -> {
                    selectAllItems()
                    return true
                }
                R.id.action_move_devices_to_group -> {
                    showMoveDevicesDialog()
                    return true
                }
                R.id.action_start_actions_activity -> {
                    if (deviceViewModel.getDeviceActiveList().isNotEmpty()) {
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
                R.id.device_groups_options -> {
                    deviceGroupOptions.showDeviceGroupsOptions()
                    return true
                }
                else -> return super.onOptionsItemSelected(item)
            }
        } ?: return super.onOptionsItemSelected(item)
    }

    private fun showMoveDevicesDialog() {
        val deviceGroups = deviceGroupsTabs.getDeviceGroups().filter { it != deviceGroupsTabs.getSelectedDeviceGroup() }
        val groupsNames = deviceGroups.map { it.groupName }.toTypedArray()

        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.move_to_group_dialog))
        builder.setItems(groupsNames) { _, which ->
            moveSelectedDevicesToDeviceGroup(deviceGroups[which])
        }
        builder.show()
    }

    private fun moveSelectedDevicesToDeviceGroup(deviceGroup : DeviceGroup) {
        val selectedDevices = deviceAdapter.getSelectedItems().map { it.device }
        addDisposable(
            deviceGroupViewModel.moveDevicesToDeviceGroup(selectedDevices,deviceGroup)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val snackbarMessage =
                        if(selectedDevices.size == 1) {
                            getString(R.string.one_device_moved_message,selectedDevices[0].name,deviceGroup.groupName)
                        } else {
                            getString(R.string.devices_moved_message,selectedDevices.size,deviceGroup.groupName)
                        }
                    Snackbar.make(container_fragment,snackbarMessage,Snackbar.LENGTH_SHORT).show()

                    exitItemSelectionState()
                    filterAndDisplayDevices(deviceViewModel.getDeviceActiveList())
                }
        )
    }


    private fun selectAllItems() {
        deviceAdapter.setItemsAsSelected(deviceAdapter.getItems())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved_devices, container, false)
    }

    private fun filterAndDisplayDevices(devices : List<DeviceActive>) {
        when {
            deviceGroupsTabs.isAllDevicesTabActive() -> {
                displayPreferredDevices(devices)
            }
            deviceGroupsTabs.isOthersTabActive() -> {
                TODO() // create use case for getting all devices without a group
            }
            else -> {
                val deviceGroup = deviceGroupsTabs.getSelectedDeviceGroup() ?: return
                deviceGroupViewModel.getDevicesFromDeviceGroup(deviceGroup.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {devicesInGroup ->
                        displayPreferredDevices(devices.filter { devicesInGroup.contains(it.device) })
                    }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("Saved devices fragment View created")

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
                        filterAndDisplayDevices(it)
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

            activity?.supportFragmentManager?.let {
                ScannedDevicesDialog().show(it, "devices_dialog")
            }
        }

        populateTabLayout()

    }


    private fun populateTabLayout() {
        tab_layout.removeAllTabs()
        deviceGroupsTabs = DeviceGroupTabs(context ?: throw IllegalStateException("Tab layout can not be populated before the fragment has been attached."),
            tab_layout)
        deviceGroupsTabs.addAllDevicesTab()

        addDisposable(
            deviceGroupViewModel.getDeviceGroups()
                .subscribe { it ->
                    it.forEach {
                    deviceGroupsTabs.addTabForDeviceGroup(it)
                }
                }
        )

        tab_layout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    filterAndDisplayDevices(deviceViewModel.getDeviceActiveList())
                }

            }
        )
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
                empty_view?.visibility = View.VISIBLE

                if(deviceGroupsTabs.isAllDevicesTabActive()) {
                    empty_view.text = getString(R.string.no_devices_plus_button_label)
                } else {
                    empty_view.text = getString(R.string.no_devices_in_group)
                }
                deviceAdapter.setDevices(listOf())
            } else {
                empty_view?.visibility = View.INVISIBLE
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

    private fun enableItemSelection(initiallySelectedItem: DeviceActive? = null) {
        deviceAdapter.enableItemSelection(initiallySelectedItem)
        selectionStateListener?.onSelectedItemsCountChanged(deviceAdapter.getNumberOfSelectedItems())
        selectionStateListener?.onItemSelectionStateEntered()
    }


    fun onBackButtonPressed(): Boolean { //returns true if it has handled the back button press
        if (deviceAdapter.isItemSelectionEnabled) {
            exitItemSelectionState()
            return true
        }
        return false
    }

    private fun exitItemSelectionState() {
        if (deviceAdapter.isItemSelectionEnabled) {
            deviceAdapter.disableItemSelection()
            selectionStateListener?.onItemSelectionStateExited()
        }
    }

    override fun onItemLongClick(item: DeviceActive) {
        if (!deviceAdapter.isItemSelectionEnabled) {
            enableItemSelection(item)
        }

//        val renameString = getString(R.string.rename)
//        val deactivateAllActionsString = getString(R.string.deactivate_all_actions)
//        val activateAllActionsString = getString(R.string.activate_all_actions)
//
//        val options = arrayOf(
//            renameString,
//            deactivateAllActionsString,
//            activateAllActionsString
//        )
//
//        val builder = AlertDialog.Builder(context)
//        builder.setTitle(R.string.actions)
//            .setItems(options) { _, which ->
//                when (options[which]) {
//                    renameString -> {
//                        createRenameDeviceDialog(item.device).show()
//                    }
//                    deactivateAllActionsString -> {
//                        setActionActiveByDeviceMacAddressUseCase.execute(item.device.macAddress, false)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe({
//                                Toast.makeText(
//                                    context,
//                                    "Deactivated all actions for device",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }, {
//                                Toast.makeText(
//                                    context,
//                                    "Error deactivating all actions for device",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            })
//                    }
//                    activateAllActionsString -> {
//                        setActionActiveByDeviceMacAddressUseCase.execute(item.device.macAddress, true)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe({
//                                Toast.makeText(
//                                    context,
//                                    "Activated all actions for device",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }, {
//                                Toast.makeText(
//                                    context,
//                                    "Error activating all actions for device",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            })
//                    }
//                }
//            }
//            .show()
    }

    @SuppressLint("InflateParams")
    fun createRenameDeviceDialog(device: Device): AlertDialog {
        val builder = AlertDialog.Builder(context)

        val inflate = layoutInflater.inflate(R.layout.layout_rename, null)
        val input = inflate.findViewById<EditText>(R.id.edit_name)
        input.setText(device.getRealName())

        return builder
            .setView(inflate)
            .setTitle("Rename Beacon")
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                input.text.toString().let { text ->
                    if (!text.isBlank()) {
                        deviceViewModel.updateDevice(device, text)
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
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
            deviceAdapter.removeItemAtPosition(position)
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

    override fun onListItemSelectionStateChanged(item: DeviceActive, state: Boolean) {
        selectionStateListener?.onSelectedItemsCountChanged(deviceAdapter.getNumberOfSelectedItems())
    }

    private inner class DeviceGroupOptions {

        fun showDeviceGroupsOptions() {
            val options =
                if(deviceGroupsTabs.isDeviceGroupTabActive())
                    arrayOf(getString(R.string.create_new_group),
                        getString(R.string.remove_group),
                        getString(R.string.edit_group_name))
                else
                    arrayOf(getString(R.string.create_new_group))


            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.device_group_options))
            builder.setItems(options) { _, which ->
                when(options[which]) {
                    getString(R.string.create_new_group) -> showCreateNewGroupDialog()
                    getString(R.string.remove_group) -> showRemoveGroupDialog()
                    getString(R.string.edit_group_name) -> showEditGroupDialog()
                }
            }
            builder.show()
        }

        @SuppressLint("InflateParams")
        private fun showEditGroupDialog() {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_group,null)

            AlertDialog.Builder(context)
                .setTitle(getString(R.string.rename_group_title))
                .setPositiveButton(getString(R.string.rename)) { _, _ ->
                    updateSelectedGroupName(dialogView.group_name.text.toString())
                }
                .setCancelable(true)
                .setView(dialogView)
                .show()
        }

        private fun updateSelectedGroupName(newName: String) {
            val deviceGroup = deviceGroupsTabs.getSelectedDeviceGroup() ?: return
            deviceGroup.groupName = newName

            addDisposable(
                deviceGroupViewModel.saveDeviceGroup(deviceGroup)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { _ ->
                        deviceGroupsTabs.updateDeviceGroup(deviceGroup)
                    }
            )
        }


        private fun showRemoveGroupDialog() {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.remove_group_title))
                .setPositiveButton(getString(R.string.remove)) { _, _ ->
                    removeCurrentGroup()
                }
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .setMessage(getString(R.string.remove_group_confirmation,
                    deviceGroupsTabs.getSelectedDeviceGroup()?.groupName ?: ""))
                .show()
        }

        private fun removeCurrentGroup() {
            val deviceGroup = deviceGroupsTabs.getSelectedDeviceGroup() ?: return
            deviceGroupViewModel.deleteDeviceGroup(deviceGroup)
            deviceGroupsTabs.removeTabForDeviceGroup(deviceGroup)

        }

        @SuppressLint("InflateParams")
        private fun showCreateNewGroupDialog() {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_group,null)

            AlertDialog.Builder(context)
                .setTitle(getString(R.string.create_new_group_title))
                .setPositiveButton(getString(R.string.create)) { _, _ ->
                    createNewGroup(dialogView.group_name.text.toString())
                }
                .setCancelable(true)
                .setView(dialogView)
                .show()
        }


        private fun createNewGroup(groupName : String) {
            addDisposable(
                deviceGroupViewModel.saveDeviceGroup(groupName)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { deviceGroup ->
                        deviceGroupsTabs.addTabForDeviceGroup(deviceGroup)
                        deviceGroupsTabs.selectTabForDeviceGroup(deviceGroup)
                    }
            )
        }


    }

    interface ItemSelectionStateListener {
        fun onItemSelectionStateEntered()
        fun onItemSelectionStateExited()
        fun onSelectedItemsCountChanged(selectedItems: Int)
    }

}



