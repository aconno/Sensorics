package com.aconno.sensorics.ui.dfu

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class NotificationActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If this activity is the root activity of the task, the app is not running
        if (isTaskRoot) {
            // Start the app before finishing
            val intent = Intent(this, DfuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtras(getIntent().extras!!) // copy all extras
            startActivity(intent)
        }

        // Now finish, which will drop you to the activity at which you were at the top of the task stack
        finish()
    }
}