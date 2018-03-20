package com.aconno.acnsensa.dagger

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorValuesUseCase
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import com.aconno.acnsensa.ui.LiveGraphActivity
import com.aconno.acnsensa.viewmodel.LiveGraphViewModel
import com.aconno.acnsensa.viewmodel.LiveGraphViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

/**
 * @author aconno
 */
@Module
class LiveGraphModule(private val liveGraphActivity: LiveGraphActivity) {

    @Provides
    @LiveGraphScope
    fun provideLiveGraphViewModelFactory(
        sensorValues: Flowable<Map<String, Number>>,
        getSensorValuesUseCase: GetSensorValuesUseCase
    ) =
        LiveGraphViewModelFactory(sensorValues, getSensorValuesUseCase)

    @Provides
    @LiveGraphScope
    fun provideLiveGraphViewModel(liveGraphViewModelFactory: LiveGraphViewModelFactory) =
        ViewModelProviders.of(
            liveGraphActivity,
            liveGraphViewModelFactory
        ).get(LiveGraphViewModel::class.java)

    @Provides
    @LiveGraphScope
    fun provideGetSensorValuesUseCase(
        inMemoryRepository: InMemoryRepository
    ) = GetSensorValuesUseCase(inMemoryRepository)
}