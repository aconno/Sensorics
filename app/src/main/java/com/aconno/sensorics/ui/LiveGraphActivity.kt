package com.aconno.sensorics.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.aconno.sensorics.R
import com.aconno.sensorics.ui.graph.BleGraph
import com.aconno.sensorics.viewmodel.LiveGraphViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_graph.*
import javax.inject.Inject

class LiveGraphActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var liveGraphViewModel: LiveGraphViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
    }

    override fun onResume() {
        super.onResume()
        val macAddress = intent.getStringExtra(MAC_ADDRESS_EXTRA)
        val graphName = intent.getStringExtra(GRAPH_NAME_EXTRA)
        liveGraphViewModel.setMacAddressAndGraphName(macAddress, graphName)
        liveGraphViewModel.getUpdates().observe(this, Observer { updateGraph() })
        loadGraph(graphName)
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

    companion object {

        private const val MAC_ADDRESS_EXTRA = "mac_address"
        private const val GRAPH_NAME_EXTRA = "graph_type"

        fun start(context: Context, macAddress: String, graphName: String) {
            val intent = Intent(context, LiveGraphActivity::class.java)
            intent.putExtra(MAC_ADDRESS_EXTRA, macAddress)
            intent.putExtra(GRAPH_NAME_EXTRA, graphName)
            context.startActivity(intent)
        }
    }
}


