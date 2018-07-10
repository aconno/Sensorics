package com.aconno.sensorics.ui

import android.content.Intent
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.aconno.sensorics.R

class SplashActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }, SPLASH_TIMEOUT)
    }

    companion object {

        private const val SPLASH_TIMEOUT = 2000L
    }
}