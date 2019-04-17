package com.aconno.sensorics.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.aconno.sensorics.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_toolbar.*

class ActionListActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar)
        toolbar.title = getString(R.string.actions)

        val fm = supportFragmentManager
        if (fm != null) {
            var fragment: Fragment? = fm.findFragmentById(R.id.content_container)
            if (fragment == null) {
                fragment = ActionListFragment.newInstance()
                fm.beginTransaction().add(R.id.content_container, fragment).commit()
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ActionListActivity::class.java)
            context.startActivity(intent)
        }
    }
}
