package com.aconno.sensorics.ui.settings.virtualscanningsources

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ActivityVirtualScanningSourceListBinding
import com.aconno.sensorics.model.BaseVirtualScanningSourceModel
import com.aconno.sensorics.model.MqttVirtualScanningSourceModel
import dagger.android.support.DaggerAppCompatActivity

class VirtualScanningSourceListActivity : DaggerAppCompatActivity(),
    VirtualScanningSourceListFragment.OnListFragmentClickListener {

    private lateinit var binding: ActivityVirtualScanningSourceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVirtualScanningSourceListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager.findFragmentById(
            R.id.virtual_scanning_sources_list_container
        ) ?: VirtualScanningSourceListFragment.newInstance().also {
            supportFragmentManager.beginTransaction()
                .add(R.id.virtual_scanning_sources_list_container, it)
                .commit()
        }

        setSupportActionBar(binding.scanningSourcesToolbar)

    }

    override fun onListFragmentClick(item: BaseVirtualScanningSourceModel?) {
        when (item) {
            is MqttVirtualScanningSourceModel -> MqttVirtualScanningSourceActivity.start(this, item)
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, VirtualScanningSourceListActivity::class.java)
            context.startActivity(intent)
        }
    }
}
