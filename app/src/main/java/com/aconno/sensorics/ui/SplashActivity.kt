package com.aconno.sensorics.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
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

        connectViewModel()
    }

    override fun onResume() {
        super.onResume()
        splashViewModel.updateAdvertisementFormats()
    }

    private fun connectViewModel() {
        //TODO: Replace Observer with LiveDataObserver.
        splashViewModel.updateCompleteEvent.observe(
            this,
            Observer { event -> event?.let { onFormatsUpdateComplete() } }
        )

        //TODO: Replace Observer with LiveDataObserver.
        splashViewModel.updateErrorEvent.observe(
            this,
            Observer { error -> error?.let { onFormatsUpdateError() } }
        )
    }

    private fun onFormatsUpdateComplete() {
        runOnUiThread {
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }, SPLASH_TIMEOUT)
        }
    }

    private fun onFormatsUpdateError() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.formats_download_error_dialog_title))
        builder.setMessage(getString(R.string.formats_download_error_dialog_message))
        builder.setNeutralButton(getString(R.string.formats_download_error_dialog_dismiss_message))
        { _, _ -> }

        val dialog = builder.create()

        dialog.show()
    }

    companion object {

        private const val SPLASH_TIMEOUT = 2000L
    }
}