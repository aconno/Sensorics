package com.aconno.sensorics.dagger.application

import android.content.SharedPreferences
import com.aconno.sensorics.data.repository.SettingsImpl
import com.aconno.sensorics.domain.repository.Settings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module()
class DataModule {

    @Provides
    @Singleton
    fun provideSettings(sharedPreferences: SharedPreferences): Settings =
        SettingsImpl(sharedPreferences)
}