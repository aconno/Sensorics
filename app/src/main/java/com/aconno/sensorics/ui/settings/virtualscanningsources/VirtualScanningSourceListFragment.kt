package com.aconno.sensorics.ui.settings.virtualscanningsources

import com.aconno.sensorics.ui.base.BaseFragment

class VirtualScanningSourceListFragment : BaseFragment() {

    private lateinit var sourcesAdapter: VirtualScanningSourcesAdapter

    companion object {
        @JvmStatic
        fun newInstance() = VirtualScanningSourceListFragment()
    }
}