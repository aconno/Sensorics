package com.aconno.sensorics.adapter

import com.aconno.sensorics.domain.model.DeviceGroup
import com.google.android.material.tabs.TabLayout
import java.lang.IllegalArgumentException

class DeviceGroupAdapter {
    var listener: DeviceGroupAdapterListener? = null
    var tabLongClickListener: DeviceGroupTabLongClickListener? = null

    private var allDevicesTabIndex: Int? = null
    lateinit var allDevicesTabName: String

    private var othersTabIndex: Int? = null
    lateinit var othersTabName: String

    private var tabToDeviceGroupMap: MutableMap<Int, DeviceGroup> = mutableMapOf()
    var selectedTabIndex = 0


    fun getTabsCount(): Int {
        var tabsCount = getDeviceGroups().size
        allDevicesTabIndex?.let { tabsCount++ }
        othersTabIndex?.let { tabsCount++ }
        return tabsCount
    }


    fun addAllDevicesTab() {
        allDevicesTabIndex = getTabsCount()
        listener?.onTabAdded(allDevicesTabIndex ?: 0)
    }

    fun addOthersTab() {
        othersTabIndex = getTabsCount()
        listener?.onTabAdded(othersTabIndex ?: 0)
    }

    fun addTabForDeviceGroup(group: DeviceGroup) {
        val tabPosition =
            if (othersTabIndex == null) { //if there is others tabs, then add tab for device group before it
                getTabsCount()
            } else {
                getTabsCount() - 1
            }

        tabToDeviceGroupMap[tabPosition] = group

        othersTabIndex?.let {
            othersTabIndex = it + 1
        }

        listener?.onTabAdded(tabPosition)
    }

    fun isAllDevicesTabActive(): Boolean {
        return selectedTabIndex == allDevicesTabIndex
    }

    fun isOthersTabActive(): Boolean {
        return selectedTabIndex == othersTabIndex
    }

    fun isDeviceGroupTabActive(): Boolean {
        return !isAllDevicesTabActive() && !isOthersTabActive()
    }

    fun getSelectedDeviceGroup(): DeviceGroup? {
        return tabToDeviceGroupMap[selectedTabIndex]
    }

    fun getDeviceGroups(): List<DeviceGroup> {
        return tabToDeviceGroupMap.entries.sortedBy { it.key }.map { it.value }
    }

    fun indexOfDeviceGroupTab(deviceGroup: DeviceGroup): Int? {
        return tabToDeviceGroupMap.filter { it.value == deviceGroup }.entries.firstOrNull()?.key
    }

    fun removeOthersTab() {
        removeTabAtIndex(othersTabIndex ?: return)
        othersTabIndex = null
    }

    private fun removeTabAtIndex(index: Int) {
        allDevicesTabIndex?.let {
            allDevicesTabIndex = if (it > index) it - 1 else it
        }
        othersTabIndex?.let {
            othersTabIndex = if (it > index) it - 1 else it
        }
        tabToDeviceGroupMap = mutableMapOf<Int, DeviceGroup>()
            .apply {
                tabToDeviceGroupMap.forEach {
                    if (it.key > index) {
                        this[it.key - 1] = it.value
                    } else if (it.key < index) {
                        this[it.key] = it.value
                    }
                }
            }

        listener?.onTabRemoved(index)
    }

    fun removeTabForDeviceGroup(deviceGroup: DeviceGroup) {
        val tabIndex = indexOfDeviceGroupTab(deviceGroup)
        tabIndex?.let { index ->
            removeTabAtIndex(index)
        }
    }

    fun updateDeviceGroup(deviceGroup: DeviceGroup) {
        val deviceGroupEntry =
            tabToDeviceGroupMap.filter { it.value.id == deviceGroup.id }.entries.firstOrNull()
                ?: return
        tabToDeviceGroupMap[deviceGroupEntry.key] = deviceGroup

        listener?.onTabChanged(deviceGroupEntry.key)
    }

    fun bindTab(tab: TabLayout.Tab, index: Int) {
        val tabName =
            when (index) {
                allDevicesTabIndex -> allDevicesTabName
                othersTabIndex -> othersTabName
                else -> tabToDeviceGroupMap[index]?.groupName
                    ?: throw IllegalArgumentException("Index is out of bounds")
            }
        tab.text = tabName

        when (index) {
            allDevicesTabIndex -> tab.view.setOnLongClickListener {
                tabLongClickListener?.onAllDevicesTabLongClick() ?: false
            }
            othersTabIndex -> tab.view.setOnLongClickListener {
                tabLongClickListener?.onOthersTabLongClick() ?: false
            }
            else -> {
                val deviceGroup = tabToDeviceGroupMap[index]
                    ?: throw IllegalArgumentException("Index is out of bounds")
                tab.view.setOnLongClickListener {
                    tabLongClickListener?.onDeviceGroupTabLongClick(deviceGroup) ?: false
                }
            }
        }
    }


    interface DeviceGroupAdapterListener {
        fun onDatasetChanged()
        fun onTabAdded(index: Int)
        fun onTabRemoved(index: Int)
        fun onTabChanged(index: Int)
    }

    interface DeviceGroupTabLongClickListener {
        fun onDeviceGroupTabLongClick(deviceGroup: DeviceGroup): Boolean
        fun onAllDevicesTabLongClick(): Boolean
        fun onOthersTabLongClick(): Boolean
    }

}