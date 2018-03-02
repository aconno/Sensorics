package com.aconno.acnsensa.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aconno.acnsensa.R
import com.aconno.acnsensa.viewmodel.BleGraph
import com.aconno.acnsensa.viewmodel.LiveGraphViewModel
import kotlinx.android.synthetic.main.activity_graph.*

/**
 * @aconno
 */
class LiveGraphActivity : AppCompatActivity() {

    //@Inject
    lateinit var liveGraphViewModel: LiveGraphViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        liveGraphViewModel = ViewModelProviders.of(this).get(LiveGraphViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        val type: Int = intent.getIntExtra(EXTRA_GRAPH_TYPE, -1)
        liveGraphViewModel.updates.observe(this, Observer { updateGraph() })
        loadGraph(type)
    }


    private fun loadGraph(type: Int) {
        val graph: BleGraph = liveGraphViewModel.getGraph(type)
        line_chart.description = graph.getDescription()
        line_chart.data = graph.lineData
        line_chart.invalidate()
    }

    private fun updateGraph() {
        line_chart.notifyDataSetChanged()
        line_chart.invalidate()
    }

    companion object {
        private const val EXTRA_GRAPH_TYPE = "com.aconno.acnsensa.EXTRA_GRAPH_TYPE"
        fun start(context: Context, type: Int) {
            val intent = Intent(context, LiveGraphActivity::class.java)
            intent.putExtra(EXTRA_GRAPH_TYPE, type)
            context.startActivity(intent)
        }
    }
}


