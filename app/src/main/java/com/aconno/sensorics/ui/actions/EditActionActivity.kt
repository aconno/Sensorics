package com.aconno.sensorics.ui.actions

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckedTextView
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.R
import com.aconno.sensorics.dagger.actionedit.DaggerEditActionComponent
import com.aconno.sensorics.dagger.actionedit.EditActionComponent
import com.aconno.sensorics.dagger.actionedit.EditActionModule
import com.aconno.sensorics.dagger.actionlist.ActionListModule
import com.aconno.sensorics.dagger.actionlist.DaggerActionListComponent
import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.ifttt.outcome.Outcome
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.ui.dialogs.SavedDevicesDialog
import com.aconno.sensorics.ui.dialogs.SavedDevicesDialogListener
import com.aconno.sensorics.viewmodel.ActionViewModel
import kotlinx.android.synthetic.main.activity_action_edit.*
import kotlinx.android.synthetic.main.content_action_detail.*
import javax.inject.Inject

class EditActionActivity : AppCompatActivity(), ConditionDialogListener,
    SavedDevicesDialogListener {

    @Inject
    lateinit var actionViewModel: ActionViewModel

    private val editActionComponent: EditActionComponent by lazy {
        val sensoricsApplication = application as? SensoricsApplication
        val actionListComponent =
            DaggerActionListComponent.builder().appComponent(sensoricsApplication?.appComponent)
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
        temperature.text = "Temperature"
        light.text = "Light"
        humidity.text = "Humidity"
        pressure.text = "Pressure"
        magnetometer_x.text = "Magnetometer X"
        magnetometer_y.text = "Magnetometer Y"
        magnetometer_z.text = "Magnetometer Z"
        accelerometer_x.text = "Accelerometer X"
        accelerometer_y.text = "Accelerometer Y"
        accelerometer_z.text = "Accelerometer Z"
        gyroscope_x.text = "Gyroscope X"
        gyroscope_y.text = "Gyroscope Y"
        gyroscope_z.text = "Gyroscope Z"
        battery_level.text = "Battery Level"

        temperature.isChecked = false
        light.isChecked = false
        humidity.isChecked = false
        pressure.isChecked = false
        magnetometer_x.isChecked = false
        magnetometer_y.isChecked = false
        magnetometer_z.isChecked = false
        accelerometer_x.isChecked = false
        accelerometer_y.isChecked = false
        accelerometer_z.isChecked = false
        gyroscope_x.isChecked = false
        gyroscope_y.isChecked = false
        gyroscope_z.isChecked = false
        battery_level.isChecked = false
    }

    private fun setConditionListeners() {
        temperature.setOnClickListener {
            openConditionDialog("Temperature")
        }
        light.setOnClickListener {
            openConditionDialog("Light")
        }
        humidity.setOnClickListener {
            openConditionDialog("Humidity")
        }
        pressure.setOnClickListener {
            openConditionDialog("Pressure")
        }
        magnetometer_x.setOnClickListener {
            openConditionDialog("Magnetometer X")
        }
        magnetometer_y.setOnClickListener {
            openConditionDialog("Magnetometer Y")
        }
        magnetometer_z.setOnClickListener {
            openConditionDialog("Magnetometer Z")
        }
        accelerometer_x.setOnClickListener {
            openConditionDialog("Accelerometer X")
        }
        accelerometer_y.setOnClickListener {
            openConditionDialog("Accelerometer Y")
        }
        accelerometer_z.setOnClickListener {
            openConditionDialog("Accelerometer Z")
        }
        gyroscope_x.setOnClickListener {
            openConditionDialog("Gyroscope X")
        }
        gyroscope_y.setOnClickListener {
            openConditionDialog("Gyroscope Y")
        }
        gyroscope_z.setOnClickListener {
            openConditionDialog("Gyroscope Z")
        }
        battery_level.setOnClickListener {
            openConditionDialog("Battery Level")
        }
    }

    private fun openConditionDialog(readingType: String) {
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

    private fun getConditionView(readingType: String): CheckedTextView {
        return when (readingType) {
            "Temperature" -> temperature
            "Light" -> light
            "Humidity" -> humidity
            "Pressure" -> pressure
            "Magnetometer X" -> magnetometer_x
            "Magnetometer Y" -> magnetometer_y
            "Magnetometer Z" -> magnetometer_z
            "Accelerometer X" -> accelerometer_x
            "Accelerometer Y" -> accelerometer_y
            "Accelerometer Z" -> accelerometer_z
            "Gyroscope X" -> gyroscope_x
            "Gyroscope Y" -> gyroscope_y
            "Gyroscope Z" -> gyroscope_z
            "Battery Level" -> battery_level
            else -> temperature
        }
    }

    override fun onSetClicked(readingType: String, constraint: String, value: String) {
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