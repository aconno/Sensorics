package com.aconno.sensorics.ui.devices

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.*
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.DeviceActiveAdapter
import com.aconno.sensorics.adapter.DeviceGroupAdapter
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
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_create_group.*
import kotlinx.android.synthetic.main.dialog_create_group.view.*
import kotlinx.android.synthetic.main.fragment_saved_devices.*
import timber.log.Timber
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SavedDevicesFragment : DaggerFragment(),
    ScannedDevicesDialogListener,
    DeviceSwipeToDismissHelper.RecyclerItemTouchHelperListener, IconInfo,
    SelectableRecyclerViewAdapter.ItemClickListener<DeviceActive>,
    SelectableRecyclerViewAdapter.ItemLongClickListener<DeviceActive>,
    SelectableRecyclerViewAdapter.ItemSelectedListener<DeviceActive>,
        DeviceGroupAdapter.DeviceGroupTabLongClickListener
{
    @Inject
    lateinit var deviceViewModel: DeviceViewModel

    @Inject
    lateinit var deviceGroupViewModel: DeviceGroupViewModel

    val deviceGroupAdapter : DeviceGroupAdapter = DeviceGroupAdapter()

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

    private var savedInstanceStateSelectedItems: Array<String>? = null

    private var savedInstanceStateSelectedTab = 0

    private var isBluetoothOn : Boolean = false //needed in order to know when to show or hide FAB (it has to be hidden during item selection state so this is neede to know if it should be shown when exiting item selection state)

    private var mainMenu: Menu? = null

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

        deviceGroupAdapter.tabLongClickListener = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        if(deviceAdapter.isItemSelectionEnabled) {
            inflater.inflate(R.menu.menu_selected_devices, menu)
            menu.findItem(R.id.action_remove_devices_from_group)?.isVisible = deviceGroupAdapter.isDeviceGroupTabActive()
            menu.findItem(R.id.action_rename_device)?.isVisible = deviceAdapter.getNumberOfSelectedItems()==1
        } else {
            inflater.inflate(R.menu.menu_devices, menu)
        }

        menu.findItem(R.id.action_start_dashboard)?.isVisible = BuildConfig.FLAVOR == "dev"

        mainMenu = menu
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
                R.id.action_remove_devices_from_group -> {
                    showRemoveDevicesFromGroupDialog()
                    return true
                }
                R.id.action_rename_device -> {
                    createRenameDeviceDialog(deviceAdapter.getSelectedItems()[0].device).show()
                    return true
                }
                R.id.action_deactivate_all_actions -> {
                    deactivateAllActionsForDevices(deviceAdapter.getSelectedItems().map { it.device })
                    exitItemSelectionState()
                    return true
                }
                R.id.action_activate_all_actions -> {
                    activateAllActionsForDevices(deviceAdapter.getSelectedItems().map { it.device })
                    exitItemSelectionState()
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
        val deviceGroups = deviceGroupAdapter.getDeviceGroups().filter { it != deviceGroupAdapter.getSelectedDeviceGroup() }
        val groupsNames = deviceGroups.map { it.groupName }.toTypedArray()

        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.move_to_group_dialog))
        builder.setItems(groupsNames) { _, which ->
            moveSelectedDevicesToDeviceGroup(deviceGroups[which])
        }
        builder.show()
    }

    private fun showRemoveDevicesFromGroupDialog() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.remove_devices_from_group_title))
            .setPositiveButton(getString(R.string.remove)) { _, _ ->
                removeSelectedDevicesFromDeviceGroup()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .setCancelable(true)
            .setMessage(getString(R.string.remove_devices_from_group_confirmation,
                deviceGroupAdapter.getSelectedDeviceGroup()?.groupName ?: ""))
            .show()
    }

    private fun removeSelectedDevicesFromDeviceGroup() {
        val selectedDevices = deviceAdapter.getSelectedItems().map { it.device }
        val deviceGroup = deviceGroupAdapter.getSelectedDeviceGroup() ?: throw IllegalStateException()
        addDisposable(
            deviceGroupViewModel.removeDevicesFromDeviceGroup(selectedDevices,deviceGroup)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val snackbarMessage =
                        if(selectedDevices.size == 1) {
                            getString(R.string.one_device_removed_from_group_message,selectedDevices[0].name,deviceGroup.groupName)
                        } else {
                            getString(R.string.devices_removed_from_group_message,selectedDevices.size,deviceGroup.groupName)
                        }
                    Snackbar.make(container_fragment,snackbarMessage,Snackbar.LENGTH_SHORT).show()

                    exitItemSelectionState()
                    filterAndDisplayDevices(deviceViewModel.getDeviceActiveList())
                }
        )
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
            deviceGroupAdapter.isAllDevicesTabActive() -> {
                displayPreferredDevices(devices.filter { !deletedItems.contains(it) })
            }
            deviceGroupAdapter.isOthersTabActive() -> {
                deviceGroupViewModel.getDevicesBelongingSomeDeviceGroup()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { devicesBelongingSomeDeviceGroup ->
                        displayPreferredDevices(devices.filter { !devicesBelongingSomeDeviceGroup.contains(it.device) && !deletedItems.contains(it) })
                    }
            }
            else -> {
                val deviceGroup = deviceGroupAdapter.getSelectedDeviceGroup() ?: return
                deviceGroupViewModel.getDevicesFromDeviceGroup(deviceGroup.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {devicesInGroup ->
                        displayPreferredDevices(devices.filter { devicesInGroup.contains(it.device) && !deletedItems.contains(it) })
                    }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("Saved devices fragment View created")

        list_devices.layoutManager = LinearLayoutManager(context)
        list_devices.adapter = deviceAdapter
        savedInstanceStateSelectedTab = 0
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ITEM_SELECTION_ENABLED_KEY)) {
                enableItemSelection()
                savedInstanceStateSelectedItems =
                    savedInstanceState.getStringArray(SELECTED_ITEMS_KEY)
            }

            savedInstanceStateSelectedTab = savedInstanceState.getInt(SELECTED_TAB_KEY)

        }

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
            exitItemSelectionState()
            listener?.onFABClicked()
            Timber.d("Button add device clicked")

            activity?.supportFragmentManager?.let {
                ScannedDevicesDialog().show(it, "devices_dialog")
            }
        }

        if(deviceGroupAdapter.getTabsCount() == 0) {
            populateDeviceGroupAdapter(savedInstanceStateSelectedTab)
        }
        tab_layout.setAdapter(deviceGroupAdapter)
        setTabSelectedListener()
    }

    private fun populateDeviceGroupAdapter(initiallySelectedTab : Int) {
        deviceGroupAdapter.allDevicesTabName = context?.getString(R.string.all_devices) ?: throw IllegalStateException()
        deviceGroupAdapter.othersTabName = context?.getString(R.string.unsorted_devices) ?: throw IllegalStateException()
        deviceGroupAdapter.addAllDevicesTab()

        addDisposable(
            deviceGroupViewModel.getDeviceGroups()
                .subscribe { it ->
                    it.forEach {
                        deviceGroupAdapter.addTabForDeviceGroup(it)
                    }

                    if(it.isNotEmpty()) {
                        deviceGroupAdapter.addOthersTab()
                    }
                    tab_layout.selectTab(initiallySelectedTab)
                }
        )


    }

    private fun setTabSelectedListener() {
        tab_layout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    snackbar?.dismiss()
                    exitItemSelectionState()
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    deviceGroupAdapter.selectedTabIndex = tab_layout.selectedTabPosition
                    if(savedInstanceStateSelectedItems != null) {
                        enableItemSelection()
                    }
                    filterAndDisplayDevices(deviceViewModel.getDeviceActiveList())
                }

            }
        )
    }

    override fun onDeviceGroupTabLongClick(deviceGroup: DeviceGroup): Boolean {
        if(deviceGroup != deviceGroupAdapter.getSelectedDeviceGroup() || deviceAdapter.isItemSelectionEnabled) {
            return false
        }
        deviceGroupOptions.showDeviceGroupsOptions()
        return true
    }

    override fun onAllDevicesTabLongClick(): Boolean {
        if(!deviceGroupAdapter.isAllDevicesTabActive() || deviceAdapter.isItemSelectionEnabled) {
            return false
        }
        deviceGroupOptions.showDeviceGroupsOptions()
        return true
    }

    override fun onOthersTabLongClick(): Boolean {
        if(!deviceGroupAdapter.isOthersTabActive() || deviceAdapter.isItemSelectionEnabled) {
            return false
        }
        deviceGroupOptions.showDeviceGroupsOptions()
        return true
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

                when {
                    deviceGroupAdapter.isAllDevicesTabActive() -> {
                        empty_view.text = getString(R.string.no_devices_plus_button_label)
                    }
                    deviceGroupAdapter.isOthersTabActive() -> {
                        empty_view.text = getString(R.string.no_unsorted_devices)
                    }
                    else -> {
                        empty_view.text = getString(R.string.no_devices_in_group)
                    }
                }
                deviceAdapter.setDevices(listOf())
            } else {
                empty_view?.visibility = View.INVISIBLE
                deviceAdapter.setDevices(preferredDevices)
                deviceAdapter.setIcons(getIconInfoForActiveDevices(preferredDevices))

                if(savedInstanceStateSelectedTab == deviceGroupAdapter.selectedTabIndex) {
                    savedInstanceStateSelectedItems?.let { selectedItems ->

                        if(deviceAdapter.isItemSelectionEnabled) {
                            deviceAdapter.setItemsAsSelected(preferredDevices.filter { selectedItems.contains(it.device.macAddress) })
                        }
                        savedInstanceStateSelectedItems = null
                    }
                }

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
        button_add_device.hide()
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

            if(isBluetoothOn) {
                button_add_device.show()
            }
        }
    }


    private fun setActionsStateForDevices(devices : List<Device>, state : Boolean, successMessage : String, failMessage : String) {
        val completables = mutableListOf<Completable>()
            .apply {
                devices.forEach {
                    add(setActionActiveByDeviceMacAddressUseCase.execute(it.macAddress,state))
                }

            }

        addDisposable(
            Completable.merge(completables)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(
                        context,
                        successMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }, {
                    Toast.makeText(
                        context,
                        failMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                })
        )
    }

    private fun activateAllActionsForDevices(devices : List<Device>) {
        val successMessage = if(devices.size==1) {
            getString(R.string.activated_all_actions_for_one_device,devices[0].name)
        } else {
            getString(R.string.activated_all_actions,devices.size)
        }

        setActionsStateForDevices(devices,true,successMessage,getString(R.string.error_activating_all_actions))
    }

    private fun deactivateAllActionsForDevices(devices : List<Device>) {
        val successMessage = if(devices.size==1) {
            getString(R.string.deactivated_all_actions_for_one_device,devices[0].name)
        } else {
            getString(R.string.deactivated_all_actions,devices.size)
        }

        setActionsStateForDevices(devices,false,successMessage,getString(R.string.error_deactivating_all_actions))
    }

    override fun onItemLongClick(item: DeviceActive) {
        if (!deviceAdapter.isItemSelectionEnabled) {
            enableItemSelection(item)
        }
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
            .setPositiveButton(getString(R.string.rename)) { dialog, _ ->
                input.text.toString().let { text ->
                    if (!text.isBlank()) {
                        deviceViewModel.updateDevice(device, text)

                        Completable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                            .subscribe {
                                exitItemSelectionState()
                            }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    fun onBluetoothOn() {
        if(!deviceAdapter.isItemSelectionEnabled) {
            button_add_device.show()
        }
        isBluetoothOn = true
    }

    fun onBluetoothOff() {
        button_add_device.hide()
        isBluetoothOn = false
    }

    override fun onDevicesDialogItemClick(item: Device) {
        addDisposable(
            deviceViewModel.saveDevice(item)
                .subscribe {
                    deviceGroupAdapter.getSelectedDeviceGroup()?.let {
                        deviceGroupViewModel.addDeviceGroupDeviceRelation(item.macAddress,it.id)
                            .subscribe()
                    }
                }
        )
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
        deviceGroupAdapter.tabLongClickListener = null
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        deviceGroupAdapter.listener = null
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
        val selectedItemsCount = deviceAdapter.getNumberOfSelectedItems()
        selectionStateListener?.onSelectedItemsCountChanged(selectedItemsCount)

        mainMenu?.findItem(R.id.action_rename_device)?.isVisible = selectedItemsCount==1
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(ITEM_SELECTION_ENABLED_KEY, deviceAdapter.isItemSelectionEnabled)
        if (deviceAdapter.isItemSelectionEnabled) {
            outState.putStringArray(
                SELECTED_ITEMS_KEY,
                deviceAdapter.getSelectedItems().map { it.device.macAddress }.toTypedArray()
            )
        }

        outState.putInt(SELECTED_TAB_KEY,deviceGroupAdapter.selectedTabIndex)
    }

    private inner class DeviceGroupOptions {

        fun showDeviceGroupsOptions() {
            val options =
                if(deviceGroupAdapter.isDeviceGroupTabActive())
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

            val dialog = AlertDialog.Builder(context)
                .setTitle(getString(R.string.rename_group_title))
                .setPositiveButton(getString(R.string.rename),null)
                .setCancelable(true)
                .setView(dialogView)
                .show()

            dialog.group_name.setText(deviceGroupAdapter.getSelectedDeviceGroup()?.groupName ?: "")
            dialog.getButton(Dialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    val newName = dialogView.group_name.text.toString()
                    val deviceGroup = deviceGroupAdapter.getSelectedDeviceGroup() ?: return@setOnClickListener

                    if(deviceGroupAdapter.getDeviceGroups().find { it.groupName == newName && it!=deviceGroup } != null) {
                        dialogView.group_name_layout.error = getString(R.string.device_group_name_taken)
                        dialogView.group_name_layout.isErrorEnabled = true
                        return@setOnClickListener
                    }
                    dialog.dismiss()

                    updateSelectedGroupName(newName)
                }
        }

        private fun updateSelectedGroupName(newName : String) {
            val deviceGroup = deviceGroupAdapter.getSelectedDeviceGroup() ?: return
            deviceGroup.groupName = newName

            addDisposable(
                deviceGroupViewModel.updateDeviceGroup(deviceGroup)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        deviceGroupAdapter.updateDeviceGroup(deviceGroup)
                    }
            )
        }


        private fun showRemoveGroupDialog() {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.remove_group_title))
                .setPositiveButton(getString(R.string.remove)) { _, _ ->
                    removeCurrentGroup()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .setCancelable(true)
                .setMessage(getString(R.string.remove_group_confirmation,
                    deviceGroupAdapter.getSelectedDeviceGroup()?.groupName ?: ""))
                .show()
        }

        private fun removeCurrentGroup() {
            val deviceGroup = deviceGroupAdapter.getSelectedDeviceGroup() ?: return
            deviceGroupViewModel.deleteDeviceGroup(deviceGroup)
            deviceGroupAdapter.removeTabForDeviceGroup(deviceGroup)

            if(deviceGroupAdapter.getDeviceGroups().isEmpty()) { //if no more device groups, no need to show others tab
                deviceGroupAdapter.removeOthersTab()
            }
        }

        @SuppressLint("InflateParams")
        private fun showCreateNewGroupDialog() {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_group,null)

            val dialog = AlertDialog.Builder(context)
                .setTitle(getString(R.string.create_new_group_title))
                .setPositiveButton(getString(R.string.create),null)
                .setCancelable(true)
                .setView(dialogView)
                .show()

            dialog.getButton(Dialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    val name = dialogView.group_name.text.toString()

                    if(deviceGroupAdapter.getDeviceGroups().find { it.groupName == name } != null) {
                        dialogView.group_name_layout.error = getString(R.string.device_group_name_taken)
                        dialogView.group_name_layout.isErrorEnabled = true
                        return@setOnClickListener
                    }
                    dialog.dismiss()

                    createNewGroup(name)
                }
        }


        private fun createNewGroup(groupName : String) {
            addDisposable(
                deviceGroupViewModel.saveDeviceGroup(groupName)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { deviceGroup ->
                        deviceGroupAdapter.addTabForDeviceGroup(deviceGroup)
                        tab_layout.selectTab(deviceGroupAdapter.indexOfDeviceGroupTab(deviceGroup) ?: 0)

                        if(deviceGroupAdapter.getDeviceGroups().size == 1) { //if this is the first device group, then it is time to show tab containing devices not belonging to any group
                            deviceGroupAdapter.addOthersTab()
                        }
                    }
            )
        }


    }

    interface ItemSelectionStateListener {
        fun onItemSelectionStateEntered()
        fun onItemSelectionStateExited()
        fun onSelectedItemsCountChanged(selectedItems: Int)
    }

    companion object {
        private const val ITEM_SELECTION_ENABLED_KEY = "ITEM_SELECTION_ENABLED_KEY"
        private const val SELECTED_ITEMS_KEY = "SELECTED_ITEMS_KEY"
        private const val SELECTED_TAB_KEY = "SELECTED_TAB_KEY"
    }

}



