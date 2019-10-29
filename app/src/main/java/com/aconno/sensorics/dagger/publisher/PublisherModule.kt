package com.aconno.sensorics.dagger.publisher

import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import dagger.Module
import dagger.Provides

@Module
class PublisherModule {


    @Provides
    @PublisherScope
    fun provideSavePublishDeviceJoinUseCase(
            publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): SavePublishDeviceJoinUseCase {
        return SavePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @PublisherScope
    fun provideDeletePublishDeviceJoinUseCase(
            publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): DeletePublishDeviceJoinUseCase {
        return DeletePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }
}