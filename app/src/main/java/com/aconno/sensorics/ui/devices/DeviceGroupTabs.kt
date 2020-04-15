package com.aconno.sensorics.ui.devices

import android.content.Context
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.DeviceGroup
import com.google.android.material.tabs.TabLayout
import java.lang.IllegalArgumentException

class DeviceGroupTabs(val context: Context, private val tabLayout: TabLayout) {
    var allDevicesTabIndex : Int? = null
    var othersTabIndex : Int? = null
    var tabToDeviceGroupMap : MutableMap<Int, DeviceGroup> = mutableMapOf()

    fun addAllDevicesTab() {
        tabLayout.addTab(tabLayout.newTab().setText(context.getString(R.string.all_devices)))
        allDevicesTabIndex = tabLayout.tabCount - 1
    }

    fun addOthersTab() {
        tabLayout.addTab(tabLayout.newTab().setText(context.getString(R.string.unsorted_devices)))
        othersTabIndex = tabLayout.tabCount - 1
    }

    fun addTabForDeviceGroup(group : DeviceGroup) {
        tabLayout.addTab(tabLayout.newTab().setText(group.groupName))
        tabToDeviceGroupMap[tabLayout.tabCount - 1] = group
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
        val tab = tabLayout.getTabAt(getIndexOfTabForDeviceGroup(deviceGroup) ?: throw IllegalArgumentException("There is no tab for specified device group"))
        tabLayout.post {
            tab?.select()
        }
    }

    private fun getIndexOfTabForDeviceGroup(deviceGroup: DeviceGroup) : Int? {
        return tabToDeviceGroupMap.filter { it.value == deviceGroup }.entries.firstOrNull()?.key
    }

    fun removeTabForDeviceGroup(deviceGroup: DeviceGroup) {
        val tabIndex = getIndexOfTabForDeviceGroup(deviceGroup)
        tabIndex?.let {index ->
            tabLayout.removeTabAt(index)
            tabLayout.selectTab(tabLayout.getTabAt(allDevicesTabIndex ?: 0))

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
    }

    fun updateDeviceGroup(deviceGroup: DeviceGroup) {
        val deviceGroupEntry = tabToDeviceGroupMap.filter { it.value.id == deviceGroup.id }.entries.firstOrNull() ?: return
        tabToDeviceGroupMap[deviceGroupEntry.key] = deviceGroup
        tabLayout.getTabAt(deviceGroupEntry.key)?.setText(deviceGroup.groupName)
    }


}