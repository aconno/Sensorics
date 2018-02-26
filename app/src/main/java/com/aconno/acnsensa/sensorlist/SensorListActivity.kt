package com.aconno.acnsensa.sensorlist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aconno.acnsensa.R
import kotlinx.android.synthetic.main.activity_main.*

class SensorListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            addFragment()
        }
    }

    private fun addFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(activity_container.id, SensorListFragment())
        transaction.commit()
    }
}
