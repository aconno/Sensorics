package com.aconno.sensorics.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.aconno.sensorics.R
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.domain.repository.AdvertisementFormatRepository
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var advertisementFormatRepository: AdvertisementFormatRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        (application as SensoricsApplication).appComponent.inject(this)

        advertisementFormatRepository.updateAdvertisementFormats()
            .subscribeOn(Schedulers.io())
            .subscribe(
                { onFormatsUpdateComplete() },
                { onFormatsUpdateError(it) }
            )
    }

    private fun onFormatsUpdateComplete() {
        Timber.e(advertisementFormatRepository.getSupportedAdvertisementFormats().toString())
        runOnUiThread {
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }, SPLASH_TIMEOUT)
        }
    }

    private fun onFormatsUpdateError(throwable: Throwable) {
        Timber.e(throwable)
        //TODO: Display error dialog.
    }

    companion object {

        private const val SPLASH_TIMEOUT = 2000L
    }
}