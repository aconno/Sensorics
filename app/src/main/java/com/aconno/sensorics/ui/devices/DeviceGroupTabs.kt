package com.aconno.sensorics.ui.devices

import android.content.Context
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.DeviceGroup
import com.google.android.material.tabs.TabLayout
import java.lang.IllegalArgumentException

class DeviceGroupTabs(val context: Context, private var tabLayout: TabLayout, private val tabLongClickListener : DeviceGroupTabLongClickListener) {
    var allDevicesTabIndex : Int? = null
    var othersTabIndex : Int? = null
    var tabToDeviceGroupMap : MutableMap<Int, DeviceGroup> = mutableMapOf()

    fun addAllDevicesTab() {
        val tab = tabLayout.newTab().setText(context.getString(R.string.all_devices))
        tab.view.setOnLongClickListener {
            tabLongClickListener.onAllDevicesTabLongClick()
        }

        tabLayout.addTab(tab)
        allDevicesTabIndex = tabLayout.tabCount - 1
    }

    fun addOthersTab() {
        val tab = tabLayout.newTab().setText(context.getString(R.string.unsorted_devices))
        tab.view.setOnLongClickListener {
            tabLongClickListener.onOthersTabLongClick()
        }

        tabLayout.addTab(tab)
        othersTabIndex = tabLayout.tabCount - 1
    }

    fun setTabLayout(tabLayout: TabLayout) {
        var tabsCount = getDeviceGroups().size
        allDevicesTabIndex?.let { tabsCount++ }
        othersTabIndex?.let { tabsCount++ }

        for(i in 0 until tabsCount) {
            when (i) {
                allDevicesTabIndex -> {
                    tabLayout.addTab(tabLayout.newTab().setText(context.getString(R.string.all_devices)))
                }
                othersTabIndex -> {
                    tabLayout.addTab(tabLayout.newTab().setText(context.getString(R.string.unsorted_devices)))
                }
                else -> {
                    tabLayout.addTab(tabLayout.newTab().setText(tabToDeviceGroupMap[i]?.groupName))
                }
            }
        }

        val selectedTabIndex = this.tabLayout.selectedTabPosition
        this.tabLayout = tabLayout

        tabLayout.getTabAt(selectedTabIndex)?.select()
    }

    fun addTabForDeviceGroup(group : DeviceGroup) {
        val tabPosition = if(othersTabIndex == null) { //if there is others tabs, then add tab for device group before it
            tabLayout.tabCount
        } else {
            tabLayout.tabCount - 1
        }

        val tab = tabLayout.newTab().setText(group.groupName)
        tab.view.setOnLongClickListener {
            tabLongClickListener.onDeviceGroupTabLongClick(group)
        }

        tabLayout.addTab(tab,tabPosition)
        tabToDeviceGroupMap[tabPosition] = group

        othersTabIndex?.let {
            othersTabIndex = it + 1
        }
    }

    fun isAllDevicesTabActive() : Boolean {
        return tabLayout.selectedTabPosition == allDevicesTabIndex
    }

    fun isOthersTabActive() : Boolean {
        return tabLayout.selectedTabPosition == othersTabIndex
    }

    fun isDeviceGroupTabActive() : Boolean {
        return !isAllDevicesTabActive() && !isOthersTabActive()
    }

    fun getSelectedDeviceGroup() : DeviceGroup? {
        return tabToDeviceGroupMap[tabLayout.selectedTabPosition]
    }

    fun selectTabForDeviceGroup(deviceGroup: DeviceGroup) {
        selectTab(getIndexOfTabForDeviceGroup(deviceGroup) ?: throw IllegalArgumentException("There is no tab for specified device group"))
    }

    fun getDeviceGroups() : List<DeviceGroup> {
        return tabToDeviceGroupMap.entries.sortedBy { it.key }.map { it.value }
    }

    private fun getIndexOfTabForDeviceGroup(deviceGroup: DeviceGroup) : Int? {
        return tabToDeviceGroupMap.filter { it.value == deviceGroup }.entries.firstOrNull()?.key
    }

    fun removeOthersTab() {
        removeTabAtIndex(othersTabIndex ?: return)
        othersTabIndex = null
    }

    private fun removeTabAtIndex(index : Int) {
        tabLayout.removeTabAt(index)

        allDevicesTabIndex?.let {
            allDevicesTabIndex = if(it > index) it-1 else it
        }
        othersTabIndex?.let {
            othersTabIndex = if(it > index) it-1 else it
        }
        tabToDeviceGroupMap = mutableMapOf<Int, DeviceGroup>()
            .apply {
                tabToDeviceGroupMap.forEach {
                    if(it.key > index) {
                        this[it.key-1] = it.value
                    } else if(it.key < index) {
                        this[it.key] = it.value
                    }
                }
            }
    }

    fun removeTabForDeviceGroup(deviceGroup: DeviceGroup) {
        val tabIndex = getIndexOfTabForDeviceGroup(deviceGroup)
        tabIndex?.let {index ->
            removeTabAtIndex(index)
        }
    }

    fun updateDeviceGroup(deviceGroup: DeviceGroup) {
        val deviceGroupEntry = tabToDeviceGroupMap.filter { it.value.id == deviceGroup.id }.entries.firstOrNull() ?: return
        tabToDeviceGroupMap[deviceGroupEntry.key] = deviceGroup
        tabLayout.getTabAt(deviceGroupEntry.key)?.setText(deviceGroup.groupName)
    }

    fun getSelectedTabIndex() : Int {
        return tabLayout.selectedTabPosition
    }

    fun selectTab(index : Int) {
        val tab = tabLayout.getTabAt(index)
        tabLayout.post {
            tab?.select()
        }
    }

    interface DeviceGroupTabLongClickListener {
        fun onDeviceGroupTabLongClick(deviceGroup: DeviceGroup) : Boolean
        fun onAllDevicesTabLongClick() : Boolean
        fun onOthersTabLongClick() : Boolean
    }
}