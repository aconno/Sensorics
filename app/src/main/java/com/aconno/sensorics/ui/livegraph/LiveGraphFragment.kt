package com.aconno.sensorics.ui.livegraph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.aconno.sensorics.R
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

        arguments?.let {
            macAddress = it.getString(MAC_ADDRESS_EXTRA)
                ?: throw IllegalStateException("$this is missing MAC_ADDRESS_EXTRA")
            graphName = it.getString(GRAPH_NAME_EXTRA)
                ?: throw IllegalStateException("$this is missing GRAPH_NAME_EXTRA")
        } ?: throw IllegalArgumentException("Arguments cannot be null for $this")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_graph, container, false)
    }

    private fun loadGraph(type: String) {
        liveGraphViewModel.getGraph(type).let { graph ->
            line_chart.description = graph.getDescription()
            line_chart.data = graph.lineData
        }

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

        // TODO: Check why onPause is not overridden
    }

    companion object {
        private const val MAC_ADDRESS_EXTRA = "mac_address"
        private const val GRAPH_NAME_EXTRA = "graph_type"

        fun newInstance(macAddress: String, graphName: String): LiveGraphFragment {
            return LiveGraphFragment().apply {
                arguments = Bundle().apply {
                    putString(MAC_ADDRESS_EXTRA, macAddress)
                    putString(GRAPH_NAME_EXTRA, graphName)
                }
            }
        }
    }
}