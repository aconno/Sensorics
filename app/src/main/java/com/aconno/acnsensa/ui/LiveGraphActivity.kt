package com.aconno.acnsensa.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.DaggerLiveGraphComponent
import com.aconno.acnsensa.dagger.LiveGraphComponent
import com.aconno.acnsensa.dagger.LiveGraphModule
import com.aconno.acnsensa.viewmodel.BleGraph
import com.aconno.acnsensa.viewmodel.LiveGraphViewModel
import kotlinx.android.synthetic.main.activity_graph.*
import javax.inject.Inject

/**
 * @aconno
 */
class LiveGraphActivity : AppCompatActivity() {

    @Inject
    lateinit var liveGraphViewModel: LiveGraphViewModel

    private val liveGraphComponent: LiveGraphComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerLiveGraphComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
            .liveGraphModule(LiveGraphModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        liveGraphComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        val type: Int = intent.getIntExtra(EXTRA_GRAPH_TYPE, -1)
        loadGraph(type)
        liveGraphViewModel.getUpdates().observe(this, Observer { updateGraph(it) })
    }


    private fun loadGraph(type: Int) {
        val graph: BleGraph = liveGraphViewModel.getGraph(type)
        line_chart.description = graph.getDescription()
        line_chart.data = graph.lineData
        line_chart.invalidate()
    }

    private fun updateGraph(long: Long?) {
        line_chart.data.notifyDataChanged()
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


