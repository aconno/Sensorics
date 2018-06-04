package com.aconno.acnsensa.dagger.livegraph

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorReadingsUseCase
import com.aconno.acnsensa.domain.interactor.filter.FilterReadingsByMacAddressUseCase
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import com.aconno.acnsensa.ui.LiveGraphActivity
import com.aconno.acnsensa.viewmodel.LiveGraphViewModel
import com.aconno.acnsensa.viewmodel.factory.LiveGraphViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

@Module
class LiveGraphModule(private val liveGraphActivity: LiveGraphActivity) {

    @Provides
    @LiveGraphScope
    fun provideLiveGraphViewModelFactory(
        sensorReadings: Flowable<List<SensorReading>>,
        filterReadingsByMacAddressUseCase: FilterReadingsByMacAddressUseCase,
        getSensorReadingsUseCase: GetSensorReadingsUseCase
    ) =
        LiveGraphViewModelFactory(
            sensorReadings,
            filterReadingsByMacAddressUseCase,
            getSensorReadingsUseCase,
            liveGraphActivity.application
        )

    @Provides
    @LiveGraphScope
    fun provideLiveGraphViewModel(liveGraphViewModelFactory: LiveGraphViewModelFactory) =
        ViewModelProviders.of(
            liveGraphActivity,
            liveGraphViewModelFactory
        ).get(LiveGraphViewModel::class.java)

    @Provides
    @LiveGraphScope
    fun provideGetSensorReadingsUseCase(
        inMemoryRepository: InMemoryRepository
    ) = GetSensorReadingsUseCase(inMemoryRepository)

    @Provides
    @LiveGraphScope
    fun provideFilterReadingsByMacAddress() = FilterReadingsByMacAddressUseCase()
}