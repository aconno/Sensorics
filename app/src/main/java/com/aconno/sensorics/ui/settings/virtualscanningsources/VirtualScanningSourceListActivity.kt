package com.aconno.sensorics.ui.settings.virtualscanningsources

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aconno.sensorics.R

class VirtualScanningSourceListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_scanning_source_list)

        supportFragmentManager.findFragmentById(
                R.id.virtual_scanning_sources_fragment
        ) ?: VirtualScanningSourceListFragment.newInstance().also {
            supportFragmentManager.beginTransaction()
                    .add(R.id.virtual_scanning_sources_fragment, it)
                    .commit()
        }

    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, VirtualScanningSourceListActivity::class.java)
            context.startActivity(intent)
        }
    }
}
