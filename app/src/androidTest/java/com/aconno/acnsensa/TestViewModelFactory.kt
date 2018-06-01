package com.aconno.acnsensa

import com.aconno.acnsensa.data.repository.AcnSensaDatabase
import com.aconno.acnsensa.model.mapper.GooglePublishDataMapper
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.model.mapper.RESTPublishDataMapper
import com.aconno.acnsensa.model.mapper.RESTPublishModelDataMapper
import com.aconno.acnsensa.viewmodel.PublishListViewModel
import com.aconno.acnsensa.viewmodel.PublishViewModel

class TestViewModelFactory {

    companion object {

        fun getPublishViewModel(acnSensaDatabase: AcnSensaDatabase): PublishViewModel {

            val googlePublishRepository =
                TestObjectFactory.getGooglePublishRepository(acnSensaDatabase)
            val restPublishRepository = TestObjectFactory.getRESTPublishRepository(acnSensaDatabase)

            return PublishViewModel(
                TestObjectFactory.getAddGooglePublishUseCase(googlePublishRepository),
                TestObjectFactory.getAddRESTPublishUseCase(restPublishRepository),
                TestObjectFactory.getUpdateGooglePublishUseCase(googlePublishRepository),
                TestObjectFactory.getUpdateRESTPublishUseCase(restPublishRepository),
                GooglePublishModelDataMapper(),
                RESTPublishModelDataMapper()
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
}