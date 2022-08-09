package com.aconno.sensorics.ui.settings.virtualscanningsources

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aconno.sensorics.R
import com.aconno.sensorics.model.BaseVirtualScanningSourceModel
import com.aconno.sensorics.model.MqttVirtualScanningSourceModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_virtual_scanning_source_list.*

class VirtualScanningSourceListActivity : DaggerAppCompatActivity(),VirtualScanningSourceListFragment.OnListFragmentClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_scanning_source_list)

        supportFragmentManager.findFragmentById(
                R.id.virtual_scanning_sources_list_container
        ) ?: VirtualScanningSourceListFragment.newInstance().also {
            supportFragmentManager.beginTransaction()
                    .add(R.id.virtual_scanning_sources_list_container, it)
                    .commit()
        }

        setSupportActionBar(scanning_sources_toolbar)

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
