package com.aconno.sensorics.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.aconno.sensorics.R
import com.aconno.sensorics.device.format.RemoteAdvertisementFormatRepository
import com.aconno.sensorics.device.format.RetrofitAdvertisementFormatApi
import com.aconno.sensorics.domain.interactor.format.GetRemoteFormatsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadRemoteFormats()
    }

    override fun onStart() {
        super.onStart()
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }, SPLASH_TIMEOUT)
    }

    private fun loadRemoteFormats() {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://playground.simvelop.de:8095")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

        val api = retrofit.create(RetrofitAdvertisementFormatApi::class.java)

        val repository = RemoteAdvertisementFormatRepository(api)

        val getRemoteFormatsUseCase = GetRemoteFormatsUseCase(repository)

        getRemoteFormatsUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> Timber.e("Result of: $result") }
    }

    companion object {

        private const val SPLASH_TIMEOUT = 2000L
    }
}