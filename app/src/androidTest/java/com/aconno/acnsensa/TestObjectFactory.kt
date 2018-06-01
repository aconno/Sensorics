package com.aconno.acnsensa

import com.aconno.acnsensa.data.mapper.GooglePublishDataMapper
import com.aconno.acnsensa.data.mapper.GooglePublishEntityDataMapper
import com.aconno.acnsensa.data.mapper.RESTPublishDataMapper
import com.aconno.acnsensa.data.mapper.RESTPublishEntityDataMapper
import com.aconno.acnsensa.data.repository.AcnSensaDatabase
import com.aconno.acnsensa.data.repository.GooglePublishRepositoryImpl
import com.aconno.acnsensa.data.repository.RESTPublishRepositoryImpl
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.ifttt.*
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.RESTPublishModel

class TestObjectFactory {

    companion object {

        fun makeGooglePublishModel(): GooglePublishModel {

            return GooglePublishModel(
                0L,
                "Test",
                "TestProjectid",
                "Region",
                "DeviceRegistry",
                "Device",
                "Key",
                false,
                "Secs",
                45000L,
                0L
            )
        }

        fun makeGooglePublishModelwithGivenId(id: Long): GooglePublishModel {

            return GooglePublishModel(
                id,
                "TestUpdate",
                "TestProjectid",
                "Region",
                "DeviceRegistry",
                "Device",
                "Key",
                false,
                "Secs",
                45000L,
                0L
            )
        }

        fun makeRESTPublishModel(): RESTPublishModel {

            return RESTPublishModel(
                0L,
                "Test",
                "url",
                "method",
                false,
                "Secs",
                45000L,
                0L
            )
        }

        fun getGooglePublishRepository(acnSensaDatabase: AcnSensaDatabase): GooglePublishRepository {
            return GooglePublishRepositoryImpl(
                acnSensaDatabase.googlePublishDao(),
                GooglePublishEntityDataMapper(),
                GooglePublishDataMapper()
            )
        }

        fun getRESTPublishRepository(acnSensaDatabase: AcnSensaDatabase): RESTPublishRepository {
            return RESTPublishRepositoryImpl(
                acnSensaDatabase.restPublishDao(),
                RESTPublishEntityDataMapper(),
                RESTPublishDataMapper()
            )
        }

        fun getAddGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): AddGooglePublishUseCase {
            return AddGooglePublishUseCase(googlePublishRepository)
        }

        fun getAddRESTPublishUseCase(restPublishRepository: RESTPublishRepository): AddRESTPublishUseCase {
            return AddRESTPublishUseCase(restPublishRepository)
        }

        fun getUpdateGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): UpdateGooglePublishUseCase {
            return UpdateGooglePublishUseCase(googlePublishRepository)
        }

        fun getUpdateRESTPublishUseCase(restPublishRepository: RESTPublishRepository): UpdateRESTPublishUserCase {
            return UpdateRESTPublishUserCase(restPublishRepository)
        }

        fun getAllGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): GetAllGooglePublishUseCase {
            return GetAllGooglePublishUseCase(googlePublishRepository)
        }

        fun getAllRESTPublishUseCase(restPublishRepository: RESTPublishRepository): GetAllRESTPublishUseCase {
            return GetAllRESTPublishUseCase(restPublishRepository)
        }

        fun getAllEnabledGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): GetAllEnabledGooglePublishUseCase {
            return GetAllEnabledGooglePublishUseCase(googlePublishRepository)
        }

        fun getAllEnabledRESTPublishUseCase(restPublishRepository: RESTPublishRepository): GetAllEnabledRESTPublishUseCase {
            return GetAllEnabledRESTPublishUseCase(restPublishRepository)
        }
    }

}