package com.aconno.sensorics.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.dialog_password.view.*

class PasswordDialog {
    companion object {
        fun create(
            forceKeyboardAppearing: Boolean = false,
            context: Context,
            action: OnPasswordDialogAction
        ): AlertDialog {
            val view: View = LayoutInflater.from(context).inflate(
                R.layout.dialog_password, null as ViewGroup?
            )

            return AlertDialog.Builder(context).setView(view)
                .setPositiveButton(R.string.confirm) { dialog, _ ->
                    action.onPasswordEntered(view.et_password.text.toString())
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    action.onDialogCancelled()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .setOnCancelListener { action.onDialogCancelled() }
                .create().apply {
                    if (forceKeyboardAppearing) {
                        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                    }
                }
        }
    }

    interface OnPasswordDialogAction {
        fun onPasswordEntered(password: String)
        fun onDialogCancelled()
    }
}