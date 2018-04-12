package com.aconno.acnsensa.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.updateaction.DaggerUpdateActionComponent
import com.aconno.acnsensa.dagger.updateaction.UpdateActionComponent
import com.aconno.acnsensa.dagger.updateaction.UpdateActionModule
import com.aconno.acnsensa.domain.ifttt.Action
import com.aconno.acnsensa.viewmodel.ExistingActionViewModel
import kotlinx.android.synthetic.main.activity_update_action.*
import javax.inject.Inject

class UpdateActionActivity : AppCompatActivity() {

    @Inject
    lateinit var existingActionViewModel: ExistingActionViewModel

    private val updateActionComponent: UpdateActionComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerUpdateActionComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
            .updateActionModule(UpdateActionModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_action)
        updateActionComponent.inject(this)

        val actionId = intent.getLongExtra(EXTRA_ACTION_ID, -1)

        update_action_button.setOnClickListener { updateAction() }
        delete_action_button.setOnClickListener { deleteAction() }

        existingActionViewModel.action.observe(this, Observer { updateFields(it) })
        existingActionViewModel.getActionById(actionId)
    }

    private fun updateAction() {
        existingActionViewModel.updateAction()
        finish()
    }

    private fun deleteAction() {
        existingActionViewModel.deleteAction()
        finish()
    }

    private fun updateFields(action: Action?) {
        action?.let {
            action_name.setText(action.name)
        }
    }


    companion object {
        private const val EXTRA_ACTION_ID = "com.aconno.acnsensa.EXTRA_ACTION_ID"

        fun start(context: Context, actionId: Long) {
            val intent = Intent(context, UpdateActionActivity::class.java)
            intent.putExtra(EXTRA_ACTION_ID, actionId)
            context.startActivity(intent)
        }
    }
}
