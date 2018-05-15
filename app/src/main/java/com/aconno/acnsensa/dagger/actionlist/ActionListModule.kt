package com.aconno.acnsensa.dagger.actionlist

import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.interactor.ifttt.GetAllActionsUseCase
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class ActionListModule {

    @Provides
    @ActionListScope
    fun provideGetAllActionsUseCase(actionsRepository: ActionsRepository): GetAllActionsUseCase {
        return GetAllActionsUseCase(actionsRepository)
    }
}