package com.aconno.acnsensa.ui.actions

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckedTextView
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.actionedit.DaggerEditActionComponent
import com.aconno.acnsensa.dagger.actionedit.EditActionComponent
import com.aconno.acnsensa.dagger.actionedit.EditActionModule
import com.aconno.acnsensa.dagger.actionlist.ActionListModule
import com.aconno.acnsensa.dagger.actionlist.DaggerActionListComponent
import com.aconno.acnsensa.domain.ifttt.Condition
import com.aconno.acnsensa.domain.ifttt.LimitCondition
import com.aconno.acnsensa.domain.ifttt.outcome.Outcome
import com.aconno.acnsensa.domain.interactor.filter.ReadingType
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.ui.dialogs.SavedDevicesDialog
import com.aconno.acnsensa.ui.dialogs.SavedDevicesDialogListener
import com.aconno.acnsensa.viewmodel.ActionViewModel
import kotlinx.android.synthetic.main.activity_action_edit.*
import kotlinx.android.synthetic.main.content_action_detail.*
import javax.inject.Inject

class EditActionActivity : AppCompatActivity(), ConditionDialogListener,
    SavedDevicesDialogListener {

    @Inject
    lateinit var actionViewModel: ActionViewModel

    private val editActionComponent: EditActionComponent by lazy {
        val acnSensaApplication = application as? AcnSensaApplication
        val actionListComponent =
            DaggerActionListComponent.builder().appComponent(acnSensaApplication?.appComponent)
                .actionListModule(ActionListModule())
                .build()
        DaggerEditActionComponent.builder().actionListComponent(actionListComponent)
            .editActionModule(EditActionModule(this))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_edit)
        editActionComponent.inject(this)

        initUi()

        actionViewModel.nameLiveData.observe(this, Observer { updateName(it) })
        actionViewModel.deviceMacAddressLiveData.observe(
            this,
            Observer { updateDeviceMacAddress(it) })
        actionViewModel.conditionLiveData.observe(this, Observer { updateConditions(it) })
        actionViewModel.outcomeLiveData.observe(this, Observer { updateOutcome(it) })

        val actionId = intent.getLongExtra(ACTION_ID_EXTRA, -1)
        actionViewModel.getAction(actionId)

        button_device.setOnClickListener {
            SavedDevicesDialog().show(supportFragmentManager, "saved_devices_dialog")
        }
    }

    private fun initUi() {
        initConditions()
        setConditionListeners()
        setOutcomeListeners()
        setButtonListeners()
    }

    private fun initConditions() {
        ReadingType.values().forEach { type ->
            val view = getConditionView(type)
            view.text = type.toString()
            view.isChecked = false
        }
    }

    private fun setConditionListeners() {
        ReadingType.values().forEach { type ->
            getConditionView(type).setOnClickListener {
                openConditionDialog(type)
            }
        }
    }

    private fun openConditionDialog(readingType: ReadingType) {
        val dialog = ConditionDialog.newInstance(readingType)
        dialog.show(supportFragmentManager, "condition_dialog_fragment")
    }

    private fun setButtonListeners() {
        save_action_button.setOnClickListener {
            actionViewModel.save(
                action_name.text.toString(),
                text_mac_address.text.toString(),
                getOutcomeType(),
                message.text.toString(),
                phone_number.text.toString()
            )
            finish()
        }
        delete_action_button.setOnClickListener {
            actionViewModel.delete()
            finish()
        }
    }

    private fun setOutcomeListeners() {
        outcome_notification.setOnClickListener {
            outcome_notification.isChecked = true
            outcome_sms.isChecked = false
            outcome_vibration.isChecked = false
            outcome_text_to_speech.isChecked = false
            showOutcomeOptions()
        }
        outcome_sms.setOnClickListener {
            outcome_notification.isChecked = false
            outcome_sms.isChecked = true
            outcome_vibration.isChecked = false
            outcome_text_to_speech.isChecked = false
            showOutcomeOptions()
        }
        outcome_vibration.setOnClickListener {
            outcome_notification.isChecked = false
            outcome_sms.isChecked = false
            outcome_vibration.isChecked = true
            outcome_text_to_speech.isChecked = false
            showOutcomeOptions()
        }
        outcome_text_to_speech.setOnClickListener {
            outcome_notification.isChecked = false
            outcome_sms.isChecked = false
            outcome_vibration.isChecked = false
            outcome_text_to_speech.isChecked = true
            showOutcomeOptions()
        }
    }

    private fun showOutcomeOptions() {
        when {
            outcome_notification.isChecked -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.GONE
            }
            outcome_sms.isChecked -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.VISIBLE
            }
            outcome_vibration.isChecked -> {
                message.visibility = View.GONE
                phone_number.visibility = View.GONE
            }
            outcome_text_to_speech.isChecked -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.GONE
            }
            else -> {
                message.visibility = View.GONE
                phone_number.visibility = View.GONE
            }
        }
    }

    private fun getOutcomeType(): Int {
        return when {
            outcome_notification.isChecked -> Outcome.OUTCOME_TYPE_NOTIFICATION
            outcome_sms.isChecked -> Outcome.OUTCOME_TYPE_SMS
            outcome_vibration.isChecked -> Outcome.OUTCOME_TYPE_VIBRATION
            outcome_text_to_speech.isChecked -> Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH
            else -> throw IllegalArgumentException("Outcome is not selected")
        }
    }

    private fun updateName(name: String?) {
        action_name.setText(name)
    }

    override fun onSavedDevicesDialogItemClick(item: Device) {
        text_mac_address.text = item.macAddress
    }

    private fun updateDeviceMacAddress(mac: String?) {
        text_mac_address.text = mac
    }

    private fun updateConditions(condition: Condition?) {
        initConditions()
        if (condition != null) {
            val conditionView = getConditionView(condition.readingType)
            conditionView.isChecked = true
            val constraintType = when (condition.type) {
                LimitCondition.LESS_THAN -> "<"
                LimitCondition.MORE_THAN -> ">"
                else -> throw IllegalArgumentException("Invalid condition type identifier ${condition.type}")
            }
            conditionView.append(" $constraintType ${condition.limit}")
        }
    }

    private fun updateOutcome(outcome: Outcome?) {
        outcome_notification.isChecked = false
        outcome_sms.isChecked = false
        outcome_vibration.isChecked = false
        outcome_text_to_speech.isChecked = false
        if (outcome != null) {
            when (outcome.type) {
                Outcome.OUTCOME_TYPE_NOTIFICATION -> {
                    outcome_notification.isChecked = true
                    phone_number.text.clear()
                    message.setText(outcome.parameters.getValue(Outcome.TEXT_MESSAGE))
                }
                Outcome.OUTCOME_TYPE_SMS -> {
                    outcome_sms.isChecked = true
                    phone_number.setText(outcome.parameters.getValue(Outcome.PHONE_NUMBER))
                    message.setText(outcome.parameters.getValue(Outcome.TEXT_MESSAGE))
                }
                Outcome.OUTCOME_TYPE_VIBRATION -> {
                    outcome_vibration.isChecked = true
                    phone_number.text.clear()
                    message.text.clear()
                }
                Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH -> {
                    outcome_text_to_speech.isChecked = true
                    phone_number.text.clear()
                    message.setText(outcome.parameters.getValue(Outcome.TEXT_MESSAGE))
                }
            }
        }
        showOutcomeOptions()
    }

    private fun getConditionView(readingType: ReadingType): CheckedTextView {
        return when (readingType) {
            ReadingType.TEMPERATURE -> temperature
            ReadingType.LIGHT -> light
            ReadingType.HUMIDITY -> humidity
            ReadingType.PRESSURE -> pressure
            ReadingType.MAGNETOMETER_X -> magnetometer_x
            ReadingType.MAGNETOMETER_Y -> magnetometer_y
            ReadingType.MAGNETOMETER_Z -> magnetometer_z
            ReadingType.ACCELEROMETER_X -> accelerometer_x
            ReadingType.ACCELEROMETER_Y -> accelerometer_y
            ReadingType.ACCELEROMETER_Z -> accelerometer_z
            ReadingType.GYROSCOPE_X -> gyroscope_x
            ReadingType.GYROSCOPE_Y -> gyroscope_y
            ReadingType.GYROSCOPE_Z -> gyroscope_z
            ReadingType.BATTERY_LEVEL -> battery_level
            ReadingType.OTHER -> temperature
        }
    }

    override fun onSetClicked(readingType: ReadingType, constraint: String, value: String) {
        actionViewModel.setCondition(readingType, constraint, value)
    }

    companion object {

        private const val ACTION_ID_EXTRA = "action_id"

        fun start(context: Context, actionId: Long) {
            val intent = Intent(context, EditActionActivity::class.java)
            intent.putExtra(ACTION_ID_EXTRA, actionId)
            context.startActivity(intent)
        }
    }
}