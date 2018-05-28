package com.aconno.acnsensa.dagger.addpublish

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.ifttt.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.AddRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateRESTPublishUserCase
import com.aconno.acnsensa.ui.settings.AddPublishActivity
import com.aconno.acnsensa.viewmodel.PublishViewModel
import com.aconno.acnsensa.viewmodel.factory.PublishViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class AddPublishModule(private val addPublishActivity: AddPublishActivity) {

    @Provides
    @AddPublishActivityScope
    fun providePublishViewModel(
        publishViewModelFactory: PublishViewModelFactory
    ) = ViewModelProviders.of(addPublishActivity, publishViewModelFactory)
        .get(PublishViewModel::class.java)

    @Provides
    @AddPublishActivityScope
    fun providePublishViewModelFactory(
        addGooglePublishUseCase: AddGooglePublishUseCase,
        addRESTPublishUseCase: AddRESTPublishUseCase,
        updateGooglePublishUseCase: UpdateGooglePublishUseCase,
        updateRESTPublishUserCase: UpdateRESTPublishUserCase
    ) =
        PublishViewModelFactory(
            addPublishActivity.application,
            addGooglePublishUseCase,
            addRESTPublishUseCase,
            updateGooglePublishUseCase,
            updateRESTPublishUserCase
        )

    @Provides
    @AddPublishActivityScope
    fun provideAddGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): AddGooglePublishUseCase {
        return AddGooglePublishUseCase(googlePublishRepository)
    }

    @Provides
    @AddPublishActivityScope
    fun provideAddRESTPublishUseCase(restPublishRepository: RESTPublishRepository): AddRESTPublishUseCase {
        return AddRESTPublishUseCase(restPublishRepository)
    }

    @Provides
    @AddPublishActivityScope
    fun provideUpdateGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): UpdateGooglePublishUseCase {
        return UpdateGooglePublishUseCase(googlePublishRepository)
    }

    @Provides
    @AddPublishActivityScope
    fun provideUpdateRESTPublishUseCase(restPublishRepository: RESTPublishRepository): UpdateRESTPublishUserCase {
        return UpdateRESTPublishUserCase(restPublishRepository)
    }
}