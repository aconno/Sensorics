package com.aconno.sensorics.dagger.worker

import com.aconno.sensorics.SyncConfigurationWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(SyncConfigurationWorker::class)
    fun bindSyncConfigurationWorker(factory: SyncConfigurationWorker.Factory): ChildWorkerFactory
}