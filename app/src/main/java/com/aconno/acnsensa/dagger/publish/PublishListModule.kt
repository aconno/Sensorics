package com.aconno.acnsensa.dagger.publish

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.ifttt.*
import com.aconno.acnsensa.model.mapper.GooglePublishDataMapper
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.model.mapper.RESTPublishDataMapper
import com.aconno.acnsensa.model.mapper.RESTPublishModelDataMapper
import com.aconno.acnsensa.ui.settings.PublishListFragment
import com.aconno.acnsensa.viewmodel.PublishListViewModel
import com.aconno.acnsensa.viewmodel.factory.PublishListViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class PublishListModule(private val publishListFragment: PublishListFragment) {

    @Provides
    @PublishListScope
    fun provideGetAllGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): GetAllGooglePublishUseCase {
        return GetAllGooglePublishUseCase(googlePublishRepository)
    }

    @Provides
    @PublishListScope
    fun provideGetAllRESTPublishUseCase(restPublishRepository: RESTPublishRepository): GetAllRESTPublishUseCase {
        return GetAllRESTPublishUseCase(restPublishRepository)
    }

    @Provides
    @PublishListScope
    fun provideUpdateGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): UpdateGooglePublishUseCase {
        return UpdateGooglePublishUseCase(googlePublishRepository)
    }

    @Provides
    @PublishListScope
    fun provideUpdateRESTPublishUseCase(restPublishRepository: RESTPublishRepository): UpdateRESTPublishUserCase {
        return UpdateRESTPublishUserCase(restPublishRepository)
    }

    @Provides
    @PublishListScope
    fun provideDeleteGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): DeleteGooglePublishUseCase {
        return DeleteGooglePublishUseCase(googlePublishRepository)
    }

    @Provides
    @PublishListScope
    fun provideDeleteRESTPublishUseCase(restPublishRepository: RESTPublishRepository): DeleteRestPublishUseCase {
        return DeleteRestPublishUseCase(restPublishRepository)
    }

    @Provides
    @PublishListScope
    fun providePublishListViewModel(
        publishViewModelFactory: PublishListViewModelFactory
    ) = ViewModelProviders.of(publishListFragment, publishViewModelFactory)
        .get(PublishListViewModel::class.java)

    @Provides
    @PublishListScope
    fun providePublishListViewModelFactory(
        getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
        getAllRESTPublishUseCase: GetAllRESTPublishUseCase,
        updateGooglePublishUseCase: UpdateGooglePublishUseCase,
        updateRESTPublishUserCase: UpdateRESTPublishUserCase,
        googlePublishDataMapper: GooglePublishDataMapper,
        googlePublishModelDataMapper: GooglePublishModelDataMapper,
        restPublishDataMapper: RESTPublishDataMapper,
        restPublishModelDataMapper: RESTPublishModelDataMapper,
        deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
        deleteRestPublishUseCase: DeleteRestPublishUseCase
    ) =
        PublishListViewModelFactory(
            getAllGooglePublishUseCase,
            getAllRESTPublishUseCase,
            updateGooglePublishUseCase,
            updateRESTPublishUserCase,
            googlePublishDataMapper,
            googlePublishModelDataMapper,
            restPublishDataMapper,
            restPublishModelDataMapper,
            deleteGooglePublishUseCase,
            deleteRestPublishUseCase
        )
}