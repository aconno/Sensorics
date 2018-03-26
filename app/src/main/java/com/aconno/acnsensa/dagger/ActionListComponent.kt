package com.aconno.acnsensa.dagger

import com.aconno.acnsensa.domain.ifttt.ActionsRespository
import com.aconno.acnsensa.domain.ifttt.GetAllActionsUseCase
import com.aconno.acnsensa.ui.ActionListFragment
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

/**
 * @aconno
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActionListScope

@Component(dependencies = [AppComponent::class], modules = [ActionListModule::class])
@ActionListScope
interface ActionListComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(actionsListFragment: ActionListFragment)
}

@Module
class ActionListModule(private val actionsListFragment: ActionListFragment) {

    @Provides
    @ActionListScope
    fun provideGetAllActionsUseCase(actionsRepository: ActionsRespository): GetAllActionsUseCase {
        return GetAllActionsUseCase(actionsRepository)
    }

}