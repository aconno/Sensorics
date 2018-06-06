package com.aconno.acnsensa

import com.aconno.acnsensa.data.repository.AcnSensaDatabase
import com.aconno.acnsensa.domain.interactor.repository.*
import com.aconno.acnsensa.model.mapper.*
import com.aconno.acnsensa.viewmodel.PublishListViewModel
import com.aconno.acnsensa.viewmodel.PublishViewModel

object TestViewModelFactory {

    fun getPublishViewModel(acnSensaDatabase: AcnSensaDatabase): PublishViewModel {

        val googlePublishRepository =
            TestObjectFactory.getGooglePublishRepository(acnSensaDatabase)
        val restPublishRepository =
            TestObjectFactory.getRESTPublishRepository(acnSensaDatabase)
        val deviceRepository =
            TestObjectFactory.getDeviceRepository(acnSensaDatabase)
        val publishDeviceJoinRepository =
            TestObjectFactory.getPublishDeviceJoinRepository(acnSensaDatabase)

        return PublishViewModel(
            TestObjectFactory.getAddGooglePublishUseCase(googlePublishRepository),
            TestObjectFactory.getAddRESTPublishUseCase(restPublishRepository),
            TestObjectFactory.getUpdateGooglePublishUseCase(googlePublishRepository),
            TestObjectFactory.getUpdateRESTPublishUseCase(restPublishRepository),
            GooglePublishModelDataMapper(),
            RESTPublishModelDataMapper(),
            SavePublishDeviceJoinUseCase(publishDeviceJoinRepository),
            DeletePublishDeviceJoinUseCase(publishDeviceJoinRepository),
            GetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository),
            GetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository),
            GetSavedDevicesMaybeUseCase(deviceRepository),
            DeviceRelationModelMapper()
        )
    }

    fun getPublishListViewModel(acnSensaDatabase: AcnSensaDatabase): PublishListViewModel {
        val googlePublishRepository =
            TestObjectFactory.getGooglePublishRepository(acnSensaDatabase)
        val restPublishRepository = TestObjectFactory.getRESTPublishRepository(acnSensaDatabase)

        return PublishListViewModel(
            TestObjectFactory.getAllGooglePublishUseCase(googlePublishRepository),
            TestObjectFactory.getAllRESTPublishUseCase(restPublishRepository),
            TestObjectFactory.getUpdateGooglePublishUseCase(googlePublishRepository),
            TestObjectFactory.getUpdateRESTPublishUseCase(restPublishRepository),
            GooglePublishDataMapper(),
            GooglePublishModelDataMapper(),
            RESTPublishDataMapper(),
            RESTPublishModelDataMapper()
        )
    }
}