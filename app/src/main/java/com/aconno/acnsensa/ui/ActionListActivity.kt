package com.aconno.acnsensa.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.aconno.acnsensa.R

class ActionListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_list)

        val fm = supportFragmentManager
        if (fm != null) {
            var fragment: Fragment? = fm.findFragmentById(R.id.action_list_container)
            if (fragment == null) {
                fragment = ActionListFragment.newInstance()
                fm.beginTransaction().add(R.id.action_list_container, fragment).commit()
            }
        }
    }
}
