package com.aconno.sensorics.data.repository

import android.content.SharedPreferences
import com.aconno.sensorics.domain.repository.Settings
import io.reactivex.Completable
import io.reactivex.Single

class SettingsImpl(
    private val sharedPreferences: SharedPreferences
) : Settings {

    override fun getLastClickedDeviceMac(): Single<String> {
        return Single.just(
            sharedPreferences.getString(MAC_ADDRESS_KEY, MAC_ADDRESS_DEFAULT_VALUE)
        )
    }

    override fun setClickedDeviceMac(macAddress: String): Completable {
        return Completable.fromAction {
            with(sharedPreferences.edit()) {
                putString(MAC_ADDRESS_KEY, macAddress)
                apply()
            }
        }
    }

    companion object {

        private const val MAC_ADDRESS_KEY = "mac_address_key"

        private const val MAC_ADDRESS_DEFAULT_VALUE = ""
    }
}