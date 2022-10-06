package com.aconno.sensorics.ui.livegraph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ActivityGraphBinding
import com.aconno.sensorics.ui.graph.BleGraph
import com.aconno.sensorics.viewmodel.LiveGraphViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class LiveGraphFragment : DaggerFragment() {

    private var binding: ActivityGraphBinding? = null

    @Inject
    lateinit var liveGraphViewModel: LiveGraphViewModel

    private lateinit var macAddress: String
    private lateinit var graphName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments == null) {
            throw IllegalArgumentException("Arguments cannot be null")
        }

        arguments?.let {
            macAddress = it.getString(MAC_ADDRESS_EXTRA, "")
            graphName = it.getString(GRAPH_NAME_EXTRA, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityGraphBinding.inflate(inflater, container, false)
        return binding?.root
    }

    private fun loadGraph(type: String) {
        val graph: BleGraph = liveGraphViewModel.getGraph(type)
        binding?.lineChart?.description = graph.getDescription()
        binding?.lineChart?.data = graph.lineData
        binding?.lineChart?.invalidate()
    }

    private fun updateGraph() {
        binding?.lineChart?.data?.notifyDataChanged()
        binding?.lineChart?.notifyDataSetChanged()
        binding?.lineChart?.invalidate()
    }

    override fun onResume() {
        super.onResume()
        liveGraphViewModel.setMacAddressAndGraphName(macAddress, graphName)
        liveGraphViewModel.getUpdates().observe(this, Observer { updateGraph() })
        loadGraph(graphName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {

        private const val MAC_ADDRESS_EXTRA = "mac_address"
        private const val GRAPH_NAME_EXTRA = "graph_type"

        fun newInstance(
            macAddress: String,
            graphName: String
        ): LiveGraphFragment {

            val fragment = LiveGraphFragment()
            val bundle = Bundle()

            bundle.putString(MAC_ADDRESS_EXTRA, macAddress)
            bundle.putString(GRAPH_NAME_EXTRA, graphName)

            fragment.arguments = bundle

            return fragment
        }
    }
}