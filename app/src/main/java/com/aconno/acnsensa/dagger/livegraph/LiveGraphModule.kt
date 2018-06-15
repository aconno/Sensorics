package com.aconno.acnsensa.dagger.livegraph

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.interactor.repository.GetReadingsUseCase
import com.aconno.acnsensa.domain.interactor.filter.FilterByMacUseCase
import com.aconno.acnsensa.domain.model.Reading
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
        readings: Flowable<List<Reading>>,
        filterByMacUseCase: FilterByMacUseCase,
        getReadingsUseCase: GetReadingsUseCase
    ) = LiveGraphViewModelFactory(
        readings,
        filterByMacUseCase,
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