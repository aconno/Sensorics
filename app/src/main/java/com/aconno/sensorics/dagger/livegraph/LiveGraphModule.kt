package com.aconno.sensorics.dagger.livegraph

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.repository.GetReadingsUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.repository.InMemoryRepository
import com.aconno.sensorics.ui.LiveGraphActivity
import com.aconno.sensorics.viewmodel.LiveGraphViewModel
import com.aconno.sensorics.viewmodel.factory.LiveGraphViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

@Module
class LiveGraphModule(private val liveGraphActivity: LiveGraphActivity) {

    @Provides
    @LiveGraphScope
    fun provideLiveGraphViewModelFactory(
        getReadingsUseCase: GetReadingsUseCase
    ) = LiveGraphViewModelFactory(
        getReadingsUseCase,
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
    ) = GetReadingsUseCase(inMemoryRepository)
}