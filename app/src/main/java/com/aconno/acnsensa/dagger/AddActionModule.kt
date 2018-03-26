package com.aconno.acnsensa.dagger

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.Action
import com.aconno.acnsensa.domain.ifttt.ActionsRespository
import com.aconno.acnsensa.domain.ifttt.AddActionUseCase
import com.aconno.acnsensa.ui.ActionViewModel
import com.aconno.acnsensa.ui.ActionViewModelFactory
import com.aconno.acnsensa.ui.AddActionActivity
import dagger.Module
import dagger.Provides
import io.reactivex.Single

/**
 * @author aconno
 */
@Module
class AddActionModule(private val addActionActivity: AddActionActivity) {

    @Provides
    @AddActionActivityScope
    fun provideActionViewModel(
        actionViewModelFactory: ActionViewModelFactory
    ) = ViewModelProviders.of(addActionActivity, actionViewModelFactory)
        .get(ActionViewModel::class.java)

    @Provides
    @AddActionActivityScope
    fun provideActionViewModelFactory(addActionUseCase: AddActionUseCase) =
        ActionViewModelFactory(addActionUseCase)

    @Provides
    @AddActionActivityScope
    fun provideAddActionUseCase(actionsRepository: ActionsRespository): AddActionUseCase {
        return AddActionUseCase(actionsRepository)
    }

    @Provides
    @AddActionActivityScope
    fun provideActionsRepository(): ActionsRespository {
        return object : ActionsRespository {
            override fun addAction(action: Action) {
                val time = System.currentTimeMillis()
                if (time % 2L == 0L) {
                    throw Exception("Mock exception")
                }
            }

            override fun deleteAction(action: Action) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getAllActions(): Single<List<Action>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }
}