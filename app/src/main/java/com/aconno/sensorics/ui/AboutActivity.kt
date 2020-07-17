package com.aconno.sensorics.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        toolbar.title = getString(R.string.about)
        developers_info.movementMethod = LinkMovementMethod.getInstance()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    companion object {

        fun start(context : Context) {
            context.startActivity(Intent(context,AboutActivity::class.java))
        }
    }
}