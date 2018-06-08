package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.ifttt.Condition
import com.aconno.acnsensa.domain.ifttt.GeneralAction
import com.aconno.acnsensa.domain.ifttt.LimitCondition
import com.aconno.acnsensa.domain.ifttt.outcome.Outcome
import com.aconno.acnsensa.domain.interactor.ifttt.AddActionUseCase
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import com.aconno.acnsensa.model.toInt
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * @author aconno
 */
class NewActionViewModel(
    private val addActionUseCase: AddActionUseCase,
    application: Application
) : AndroidViewModel(application) {

    val addActionResults: MutableLiveData<Boolean> = MutableLiveData()

    private var condition: Condition? = null

    fun setCondition(sensorType: SensorTypeSingle, constraint: String, constraintValue: String) {
        //TODO: Fix this try catch, it catches the exception when constraintValue is empty string
        try {
            when (constraint) {
                "<" -> condition = LimitCondition(
                    sensorType,
                    constraintValue.toFloat(),
                    LimitCondition.LESS_THAN
                )
                ">" -> condition = LimitCondition(
                    sensorType,
                    constraintValue.toFloat(),
                    LimitCondition.MORE_THAN
                )
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getCondition(): Condition? {
        return condition
    }

    fun addAction(
        name: String,
        deviceMacAddress: String,
        outcomeType: String,
        smsDestination: String,
        content: String
    ) {
        try {
            val newId = 0L
            val condition = this.condition

            if (condition == null) {
                Timber.d("Condition is null. Cannot save action.")
            } else {

                val parameters = mapOf(
                    Pair(Outcome.TEXT_MESSAGE, content), Pair(Outcome.PHONE_NUMBER, smsDestination)
                )

                val outcomeEndType = when (outcomeType) {
                    getApplication<Application>().getString(R.string.phone_notification) -> {
                        Outcome.OUTCOME_TYPE_NOTIFICATION
                    }
                    getApplication<Application>().getString(R.string.sms_message) -> {
                        Outcome.OUTCOME_TYPE_SMS
                    }
                    getApplication<Application>().getString(R.string.vibration) -> {
                        Outcome.OUTCOME_TYPE_VIBRATION
                    }
                    getApplication<Application>().getString(R.string.text_to_speech) -> {
                        Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH
                    }
                    else -> throw IllegalArgumentException("Invalid outcome type")
                }

                val outcome = Outcome(parameters, outcomeEndType)
                val newAction = GeneralAction(newId, name, deviceMacAddress, condition, outcome)

                addActionUseCase.execute(newAction)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ onAddActionSuccess() }, { onAddActionFail() })
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }


    private fun onAddActionSuccess() {
        addActionResults.value = true
    }

    private fun onAddActionFail() {
        addActionResults.value = false
    }
}
