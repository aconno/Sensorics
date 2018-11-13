package com.aconno.sensorics.ui.livegraph

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.ui.graph.BleGraph
import com.aconno.sensorics.viewmodel.LiveGraphViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_graph.*
import javax.inject.Inject

class LiveGraphFragment : DaggerFragment() {

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
        return inflater.inflate(R.layout.activity_graph, container, false)
    }

    private fun loadGraph(type: String) {
        val graph: BleGraph = liveGraphViewModel.getGraph(type)
        line_chart.description = graph.getDescription()
        line_chart.data = graph.lineData
        line_chart.invalidate()
    }

    private fun updateGraph() {
        line_chart.data?.notifyDataChanged()
        line_chart.notifyDataSetChanged()
        line_chart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        liveGraphViewModel.setMacAddressAndGraphName(macAddress, graphName)
        liveGraphViewModel.getUpdates().observe(this, Observer { updateGraph() })
        loadGraph(graphName)
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