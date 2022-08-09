package com.aconno.sensorics.ui.devices

import android.content.Context
import android.util.AttributeSet
import com.aconno.sensorics.adapter.DeviceGroupAdapter
import com.google.android.material.tabs.TabLayout

class DeviceGroupTabLayout(context : Context, attrs : AttributeSet?) : TabLayout(context,attrs), DeviceGroupAdapter.DeviceGroupAdapterListener {
    private lateinit var adapter : DeviceGroupAdapter

    fun setAdapter(adapter: DeviceGroupAdapter) {
        this.adapter = adapter
        adapter.listener = this
        addTabsFromAdapter()
    }

    private fun addTabsFromAdapter() {
        removeAllTabs()
        for(i in 0 until adapter.getTabsCount()) {
            onTabAdded(i)
        }
    }

    override fun onDatasetChanged() {
        addTabsFromAdapter()
    }

    override fun onTabAdded(index: Int) {
        val tab = newTab()
        adapter.bindTab(tab,index)
        addTab(tab,index)
    }

    override fun onTabRemoved(index: Int) {
        removeTabAt(index)
    }

    override fun onTabChanged(index: Int) {
        getTabAt(index)?.let {
            adapter.bindTab(it,index)
        }

    }

    fun selectTab(index: Int) {
        val tab = getTabAt(index)
        post {
            tab?.select()
        }
    }

}