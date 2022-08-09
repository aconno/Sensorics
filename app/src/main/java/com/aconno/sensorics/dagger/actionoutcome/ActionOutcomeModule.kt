package com.aconno.sensorics.dagger.actionoutcome

import com.aconno.sensorics.domain.AlarmServiceController
import com.aconno.sensorics.domain.SmsSender
import com.aconno.sensorics.domain.Vibrator
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.ifttt.NotificationDisplay
import com.aconno.sensorics.domain.ifttt.TextToSpeechPlayer
import com.aconno.sensorics.domain.ifttt.outcome.*
import com.aconno.sensorics.domain.interactor.ifttt.InputToOutcomesUseCase
import com.aconno.sensorics.domain.interactor.time.GetLocalTimeOfDayInSecondsUseCase
import dagger.Module
import dagger.Provides

@Module
class ActionOutcomeModule {

    @Provides
    @ActionOutcomeScope
    fun provideHandleInputUseCase(
            actionResolver: InputToActionsResolver,
            getLocalTimeOfDayInSecondsUseCase: GetLocalTimeOfDayInSecondsUseCase
    ): InputToOutcomesUseCase {
        return InputToOutcomesUseCase(actionResolver, getLocalTimeOfDayInSecondsUseCase)
    }

    @Provides
    @ActionOutcomeScope
    fun provideActionResolver(
        actionsRepository: ActionsRepository
    ): InputToActionsResolver {
        return InputToActionsResolverImpl(actionsRepository)
    }


    @Provides
    @ActionOutcomeScope
    fun provideRunOutcomeUseCase(
            notificationDisplay: NotificationDisplay,
            textToSpeechPlayer: TextToSpeechPlayer,
            vibrator: Vibrator,
            alarmServiceController: AlarmServiceController,
            smsSender: SmsSender
    ): RunOutcomeUseCase {
        val notificationOutcomeExecutor = NotificationOutcomeExecutor(notificationDisplay)
        val textToSpeechOutcomeExecutor = TextToSpeechOutcomeExecutor(textToSpeechPlayer)
        val vibrationOutcomeExecutor = VibrationOutcomeExecutor(vibrator)
        val alarmOutcomeExecutor = AlarmOutcomeExecutor(alarmServiceController)
        val smsOutcomeExecutor = SmsOutcomeExecutor(smsSender)

        val outcomeExecutorSelector = OutcomeExecutorSelector(
                notificationOutcomeExecutor,
                textToSpeechOutcomeExecutor,
                vibrationOutcomeExecutor,
                alarmOutcomeExecutor,
                smsOutcomeExecutor
        )

        return RunOutcomeUseCase(outcomeExecutorSelector)
    }
}