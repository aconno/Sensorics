package com.aconno.sensorics.dagger.logging

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.logs.AddLogUseCase
import com.aconno.sensorics.domain.interactor.logs.DeleteDeviceLogsUseCase
import com.aconno.sensorics.domain.interactor.logs.GetDeviceLogsUseCase
import com.aconno.sensorics.domain.logs.LogsRepository
import com.aconno.sensorics.model.mapper.LogModelMapper
import com.aconno.sensorics.ui.logs.LoggingActivity
import com.aconno.sensorics.viewmodel.LoggingViewModel
import com.aconno.sensorics.viewmodel.factory.LoggingViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class LoggingModule {

    @Provides
    @LoggingScope
    fun provideLoggingViewModel(
            loggingActivity: LoggingActivity,
            loggingViewModelFactory: LoggingViewModelFactory
    ) = ViewModelProviders.of(loggingActivity, loggingViewModelFactory)
            .get(LoggingViewModel::class.java)

    @Provides
    @LoggingScope
    fun provideLoggingViewModelFactory(getDeviceLogsUseCase: GetDeviceLogsUseCase,
                                       deleteDeviceLogsUseCase: DeleteDeviceLogsUseCase,
                                       addLogUseCase: AddLogUseCase,
                                       logModelMapper: LogModelMapper
    ) = LoggingViewModelFactory(getDeviceLogsUseCase, deleteDeviceLogsUseCase, addLogUseCase, logModelMapper)

    @Provides
    @LoggingScope
    fun provideGetDeviceLogsUseCase(logsRepository: LogsRepository): GetDeviceLogsUseCase {
        return GetDeviceLogsUseCase(logsRepository)
    }

    @Provides
    @LoggingScope
    fun providesDeleteDeviceLogsUseCase(logsRepository: LogsRepository): DeleteDeviceLogsUseCase {
        return DeleteDeviceLogsUseCase(logsRepository)
    }

    @Provides
    @LoggingScope
    fun providesAddLogUseCase(logsRepository: LogsRepository): AddLogUseCase {
        return AddLogUseCase(logsRepository)
    }
}