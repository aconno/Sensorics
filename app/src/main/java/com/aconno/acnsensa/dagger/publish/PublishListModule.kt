package com.aconno.acnsensa.dagger.publish

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.interactor.ifttt.GetAllGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateGooglePublishUseCase
import com.aconno.acnsensa.ui.settings.PublishFragment
import com.aconno.acnsensa.viewmodel.PublishListViewModel
import com.aconno.acnsensa.viewmodel.factory.PublishListViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class PublishListModule(private val publishFragment: PublishFragment) {

    @Provides
    @PublishListScope
    fun provideGetAllGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): GetAllGooglePublishUseCase {
        return GetAllGooglePublishUseCase(googlePublishRepository)
    }

    @Provides
    @PublishListScope
    fun provideUpdateGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): UpdateGooglePublishUseCase {
        return UpdateGooglePublishUseCase(googlePublishRepository)
    }

    @Provides
    @PublishListScope
    fun providePublishListViewModel(
        publishViewModelFactory: PublishListViewModelFactory
    ) = ViewModelProviders.of(publishFragment, publishViewModelFactory)
        .get(PublishListViewModel::class.java)

    @Provides
    @PublishListScope
    fun providePublishListViewModelFactory(
        getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
        updateGooglePublishUseCase: UpdateGooglePublishUseCase
    ) =
        PublishListViewModelFactory(
            publishFragment.activity?.application as Application,
            getAllGooglePublishUseCase,
            updateGooglePublishUseCase
        )
}