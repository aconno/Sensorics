package com.aconno.sensorics.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        binding.toolbar.title = getString(R.string.about)
        binding.developersInfo.movementMethod = LinkMovementMethod.getInstance()
        binding.forMoreImprintInfo.movementMethod = LinkMovementMethod.getInstance()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }
    }
}