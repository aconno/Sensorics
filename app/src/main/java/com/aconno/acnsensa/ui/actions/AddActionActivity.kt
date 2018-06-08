package com.aconno.acnsensa.ui.actions

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckedTextView
import android.widget.TextView
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.actionlist.ActionListModule
import com.aconno.acnsensa.dagger.actionlist.DaggerActionListComponent
import com.aconno.acnsensa.dagger.addaction.AddActionComponent
import com.aconno.acnsensa.dagger.addaction.AddActionModule
import com.aconno.acnsensa.dagger.addaction.DaggerAddActionComponent
import com.aconno.acnsensa.domain.ifttt.Condition
import com.aconno.acnsensa.domain.ifttt.LimitCondition
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import com.aconno.acnsensa.model.toSensorType
import com.aconno.acnsensa.model.toStringResource
import com.aconno.acnsensa.ui.dialogs.SavedDevicesDialog
import com.aconno.acnsensa.ui.dialogs.SavedDevicesDialogListener
import com.aconno.acnsensa.viewmodel.NewActionViewModel
import kotlinx.android.synthetic.main.activity_action_add.*
import kotlinx.android.synthetic.main.content_action_detail.*
import javax.inject.Inject


class AddActionActivity : AppCompatActivity(), ConditionDialogListener, SavedDevicesDialogListener {

    @Inject
    lateinit var newActionViewModel: NewActionViewModel

    private val addActionComponent: AddActionComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication

        val actionListComponent =
            DaggerActionListComponent.builder().appComponent(acnSensaApplication?.appComponent)
                .actionListModule(ActionListModule())
                .build()

        DaggerAddActionComponent.builder().actionListComponent(actionListComponent)
            .addActionModule(AddActionModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_add)
        addActionComponent.inject(this)

        setConditionChipOnClickListeners()
        initConditionViews()

        add_action_button.setOnClickListener { this.addAction() }

        setOutcomeChipOnClickListeners()

        button_device.setOnClickListener {
            SavedDevicesDialog().show(supportFragmentManager, "saved_devices_dialog")
        }
    }

    private fun setOutcomeChipOnClickListeners() {
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

    private fun setConditionChipOnClickListeners() {
        SensorTypeSingle.values().forEach { sensorType ->
            getConditionView(sensorType).setOnClickListener {
                openConditionDialog(sensorType)
            }
        }
    }

    override fun onSavedDevicesDialogItemClick(item: Device) {
        text_mac_address.text = item.macAddress
    }

    private fun openConditionDialog(sensorType: SensorTypeSingle) {
        val dialog = ConditionDialog.newInstance(sensorType)
        dialog.show(supportFragmentManager, "condition_dialog_fragment")
    }

    override fun onResume() {
        super.onResume()
        newActionViewModel.addActionResults.observe(this, Observer { onAddActionResult(it) })
    }

    private fun addAction() {
        val name = action_name.text.toString()
        val deviceMacAddress = text_mac_address.text.toString()
        val outcome = getOutcome()
        val smsDestination = phone_number.text.toString()
        val content = message.text.toString()

        newActionViewModel.addAction(
            name,
            deviceMacAddress,
            outcome,
            smsDestination,
            content
        )
    }

    private fun getOutcome(): String {
        if (outcome_notification.isChecked) {
            return "Phone Notification"
        }
        if (outcome_sms.isChecked) {
            return "SMS Message"
        }
        if (outcome_vibration.isChecked) {
            return "Vibration"
        }
        if (outcome_text_to_speech.isChecked) {
            return "Text to Speech"
        }
        return ""
    }

    private fun onAddActionResult(success: Boolean?) {
        when (success) {
            true -> finish()
            else -> Toast.makeText(
                this,
                "Failed to makeServiceNotificationChannel Action",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //TODO: Duplicate code with UpdateActionActivity.
    private fun showOutcomeOptions() {
        val selected = getOutcome()
        when (selected) {
            "Phone Notification" -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.GONE
            }
            "SMS Message" -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.VISIBLE
            }
            "Vibration" -> {
                message.visibility = View.GONE
                phone_number.visibility = View.GONE
            }
            "Text to Speech" -> {
                message.visibility = View.VISIBLE
                phone_number.visibility = View.GONE
            }
        }
    }

    override fun onSetClicked(sensorType: SensorTypeSingle, constraint: String, value: String) {
        initConditionViews()
        //TODO: Prevent passing an empty string value
        newActionViewModel.setCondition(sensorType, constraint, value)
        val condition = newActionViewModel.getCondition()
        condition?.let {
            setSelectedCondition(condition)
        }
    }


    private fun initConditionViews() {
        initConditionTexts()
        initConditionStates()
    }

    private fun initConditionTexts() {
        SensorTypeSingle.values().forEach {
            getConditionView(it).text = it.toStringResource(this)
        }
    }

    private fun initConditionStates() {
        SensorTypeSingle.values().forEach {
            getConditionView(it).isChecked = false
        }
    }

    private fun setSelectedCondition(condition: Condition) {
        val conditionView = getConditionView(condition.sensorType)
        conditionView.isChecked = true
        appendConditionString(conditionView, condition)
    }

    private fun appendConditionString(textView: TextView, condition: Condition) {
        val constraint = when (condition.type) {
            LimitCondition.LESS_THAN -> "<"
            LimitCondition.MORE_THAN -> ">"
            else -> throw IllegalArgumentException("Invalid constraint identifier ${condition.type}")
        }
        textView.text = "${textView.text} $constraint ${condition.limit}"
    }

    private fun getConditionView(sensorType: SensorTypeSingle): CheckedTextView {
        return when (sensorType) {
            SensorTypeSingle.TEMPERATURE -> temperature
            SensorTypeSingle.LIGHT -> light
            SensorTypeSingle.HUMIDITY -> humidity
            SensorTypeSingle.PRESSURE -> pressure
            SensorTypeSingle.MAGNETOMETER_X -> magnetometer_x
            SensorTypeSingle.MAGNETOMETER_Y -> magnetometer_y
            SensorTypeSingle.MAGNETOMETER_Z -> magnetometer_z
            SensorTypeSingle.ACCELEROMETER_X -> accelerometer_x
            SensorTypeSingle.ACCELEROMETER_Y -> accelerometer_y
            SensorTypeSingle.ACCELEROMETER_Z -> accelerometer_z
            SensorTypeSingle.GYROSCOPE_X -> gyroscope_x
            SensorTypeSingle.GYROSCOPE_Y -> gyroscope_y
            SensorTypeSingle.GYROSCOPE_Z -> gyroscope_z
            SensorTypeSingle.BATTERY_LEVEL -> battery_level
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AddActionActivity::class.java)
            context.startActivity(intent)
        }
    }
}