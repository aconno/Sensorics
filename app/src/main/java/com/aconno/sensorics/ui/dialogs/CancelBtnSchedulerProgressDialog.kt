package com.aconno.sensorics.ui.dialogs


import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.aconno.sensorics.databinding.DialogIndeterminateProgressBinding
import com.aconno.sensorics.domain.DisposableValue

class CancelBtnSchedulerProgressDialog(
    private val activity: Activity,
    private val handler: Handler,
    private val cancelBanAppearAfter: Long = 15000,
    private val cancelledByUser: () -> Unit
) : AlertDialog(activity) {

    private lateinit var binding: DialogIndeterminateProgressBinding

    private lateinit var cancelBtn: Button
    private var disposableMessage: DisposableValue<String>? = null
    var progressMessage: String = ""
        set(value) {
            setMessageOrScheduleWhenInitialized(value)
            rescheduleCancelTask()
            field = value
        }

    private fun setMessageOrScheduleWhenInitialized(msg: String) {
        binding.message.text = msg
    }

    private var cancelBtnAppearingRunnable = Runnable {
        val fadeTransition = Fade()
        fadeTransition.duration = 3000
        TransitionManager.beginDelayedTransition(binding.root, fadeTransition)
        cancelBtn.visibility = View.VISIBLE
    }

    private fun rescheduleCancelTask() {
        handler.removeCallbacks(cancelBtnAppearingRunnable)
        scheduleCancelTaskIfRunning()
    }

    private fun scheduleCancelTaskIfRunning() {
        if (!activity.isFinishing and !activity.isDestroyed) {
            handler.postDelayed(cancelBtnAppearingRunnable, cancelBanAppearAfter)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        binding = DialogIndeterminateProgressBinding.inflate(layoutInflater)

        cancelBtn = binding.cancelBtn
        setView(binding.root)

        setOnDismissListener {
            handler.removeCallbacksAndMessages(null)
        }
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        this@CancelBtnSchedulerProgressDialog.cancelBtn.setOnClickListener {
            cancelledByUser()
            dismiss()
        }
        binding.message.text = disposableMessage?.value
        super.onCreate(savedInstanceState)
    }

    override fun show() {
        scheduleCancelTaskIfRunning()
        super.show()
    }

    override fun dismiss() {
        handler.removeCallbacks(cancelBtnAppearingRunnable)
        super.dismiss()
    }
}