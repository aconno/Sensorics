package com.aconno.sensorics.data.repository.resources

import com.aconno.sensorics.data.api.ResourcesApi
import com.aconno.sensorics.domain.repository.*

class ResourcesRepositoryImpl(val cacheFilePath: String, val api: ResourcesApi) :
    ResourcesRepository,
    ConfigRepository, FormatRepository, MainScreenRepository, UseCaseRepository {

    override fun sync() {

    }

    override fun addConfig() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getConfigs() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addFormat() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFormats() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addMainScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addUseCase() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}