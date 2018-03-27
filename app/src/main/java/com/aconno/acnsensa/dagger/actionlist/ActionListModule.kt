package com.aconno.acnsensa.dagger.actionlist

import com.aconno.acnsensa.domain.ifttt.ActionsRespository
import com.aconno.acnsensa.domain.ifttt.GetAllActionsUseCase
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class ActionListModule {

    @Provides
    @ActionListScope
    fun provideGetAllActionsUseCase(actionsRepository: ActionsRespository): GetAllActionsUseCase {
        return GetAllActionsUseCase(actionsRepository)
    }

}