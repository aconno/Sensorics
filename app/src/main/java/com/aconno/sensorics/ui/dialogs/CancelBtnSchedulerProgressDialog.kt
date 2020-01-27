package com.aconno.sensorics.ui.dialogs


import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.DisposableValue
import kotlinx.android.synthetic.main.dialog_indeterminate_progress.*
import kotlinx.android.synthetic.main.dialog_indeterminate_progress.view.*

class CancelBtnSchedulerProgressDialog(
    private val activity: Activity,
    private val handler: Handler,
    private val cancelBanAppearAfter: Long = 15000
) : AlertDialog(activity) {

    private lateinit var cancelBtn: Button
    private var disposableMessage: DisposableValue<String>? = null
    var progressMessage: String = ""
        set(value) {
            setMessageOrScheduleWhenInitialized(value)
            field = value
        }

    private fun setMessageOrScheduleWhenInitialized(msg: String) {
        if (message != null) {
            message.text = msg
        } else {
            disposableMessage = DisposableValue(msg)
        }
    }

    private var cancelBtnAppearingRunnable = Runnable {
        val fadeTransition = Fade()
        fadeTransition.duration = 3000
        TransitionManager.beginDelayedTransition(root, fadeTransition)
        cancelBtn.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val view = layoutInflater.inflate(
            R.layout.dialog_indeterminate_progress,
            null as ViewGroup?
        )
        cancelBtn = view.cancelBtn
        setView(view)
        setOnDismissListener {
            handler.removeCallbacksAndMessages(null)
        }
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        this@CancelBtnSchedulerProgressDialog.cancelBtn.setOnClickListener { dismiss() }
        view.message.text = disposableMessage?.value
        super.onCreate(savedInstanceState)
    }

    override fun show() {
        if (!activity.isFinishing and !activity.isDestroyed) {
            handler.postDelayed(cancelBtnAppearingRunnable, cancelBanAppearAfter)
            super.show()
        }
    }

    override fun dismiss() {
        handler.removeCallbacks(cancelBtnAppearingRunnable)
        super.dismiss()
    }
}