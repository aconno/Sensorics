package com.aconno.acnsensa.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aconno.acnsensa.R
import com.aconno.acnsensa.viewmodel.LiveGraphViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.LineData
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
        val type: Int = intent.getIntExtra(EXTRA_GRAPH_TYPE, -1)
        displayGraph(type)
    }

    private fun displayGraph(type: Int) {
        val description = Description()
        description.text = "Hardcoded description: $type "
        line_chart.description = description
        line_chart.data = LineData()
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