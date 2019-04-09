package com.aconno.sensorics.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.R
import com.aconno.sensorics.viewmodel.SplashViewModel
import com.aconno.sensorics.viewmodel.factory.SplashViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class SplashActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var splashViewModelFactory: SplashViewModelFactory

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashViewModel = ViewModelProviders.of(this, splashViewModelFactory)
            .get(SplashViewModel::class.java)

        splashViewModel.initializationLiveEvent.observe(this, Observer {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })

        splashViewModel.initApp()
    }
}