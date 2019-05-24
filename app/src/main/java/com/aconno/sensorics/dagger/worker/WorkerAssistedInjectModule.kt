package com.aconno.sensorics.dagger.worker

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@Module(includes = [AssistedInject_WorkerAssistedInjectModule::class, SyncConfigurationServiceModule::class])
@AssistedModule
interface WorkerAssistedInjectModule