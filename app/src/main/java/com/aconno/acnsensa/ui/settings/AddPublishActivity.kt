package com.aconno.acnsensa.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.addpublish.AddPublishComponent
import com.aconno.acnsensa.dagger.addpublish.AddPublishModule
import com.aconno.acnsensa.dagger.addpublish.DaggerAddPublishComponent
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.viewmodel.PublishViewModel
import kotlinx.android.synthetic.main.activity_add_publish.*
import javax.inject.Inject

class AddPublishActivity : AppCompatActivity() {


    @Inject
    lateinit var publishViewModel: PublishViewModel

    private var basePublish: BasePublish? = null

    private val addPublishComponent: AddPublishComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication

        DaggerAddPublishComponent.builder().appComponent(acnSensaApplication?.appComponent)
            .addPublishModule(AddPublishModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_publish)
        addPublishComponent.inject(this)
        val temp = intent.getSerializableExtra(ADD_PUBLISH_ACTIVITY_KEY)

        setSupportActionBar(custom_toolbar)

        when {
            temp is GooglePublish -> {
                basePublish = temp
                setTextsWithTemp()
            }
            temp != null -> throw IllegalArgumentException("Only GooglePublish can be sent")
        }

        initViews()
    }

    private fun initViews() {

        edit_privatekey.setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(
                intent,
                PICKFILE_REQUEST_CODE
            )

        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> layout_google.visibility = View.VISIBLE
                    else -> layout_google.visibility = View.GONE
                }
            }
        }
    }

    /**
     * This method is called after @Intent.ACTION_GET_CONTENT result is returned.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICKFILE_REQUEST_CODE) {
            data?.let {
                val path = it.data.path
                edit_privatekey.setText(path)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setTextsWithTemp() {
        edit_name.setText(basePublish?.name)

        if (basePublish is GooglePublish) {
            layout_google.visibility = View.VISIBLE

            val googlePublish = basePublish as GooglePublish

            edit_projectid.setText(googlePublish.projectId)
            edit_region.setText(googlePublish.region)
            edit_deviceregistry.setText(googlePublish.deviceRegistry)
            edit_device.setText(googlePublish.device)
            edit_privatekey.setText(googlePublish.privateKey)
        } else {
            //TODO
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_publish_menu, menu)

        if (menu != null) {
            val item = menu.findItem(R.id.action_publish_done)
            if (basePublish != null) {
                item.title = "Update"
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
        //TODO Add startActivityForResult
            R.id.action_publish_done -> addOrUpdate()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun addOrUpdate() {
        val selectedItem = spinner.selectedItemPosition

        when (selectedItem) {
            0 -> googleAddOrUpdate()
            1 -> otherAddOrUpdate()
            else -> throw IllegalArgumentException("Please use registered types.")
        }

        //After save or update finish activity
        finish()
    }

    private fun otherAddOrUpdate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun googleAddOrUpdate() {
        val name = edit_name.text.toString().trim()
        val projectId = edit_projectid.text.toString().trim()
        val region = edit_region.text.toString().trim()
        val deviceRegistry = edit_deviceregistry.text.toString().trim()
        val device = edit_device.text.toString().trim()
        val privateKey = edit_privatekey.text.toString().trim()

        if (
            name.isBlank() ||
            projectId.isBlank() ||
            region.isBlank() ||
            deviceRegistry.isBlank() ||
            device.isBlank() ||
            privateKey.isBlank()
        ) {
            //TODO Error
            Toast.makeText(this, "Please fill the blanks", Toast.LENGTH_SHORT).show()
        } else {

            val toastText: String

            if (basePublish != null) {
                val id = basePublish!!.id

                toastText = "Updated"
                publishViewModel.update(
                    id,
                    name,
                    projectId,
                    region,
                    deviceRegistry,
                    device,
                    privateKey
                )

            } else {
                toastText = "Created"
                publishViewModel.save(
                    name,
                    projectId,
                    region,
                    deviceRegistry,
                    device,
                    privateKey
                )
            }

            Toast.makeText(this, "$toastText $name", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        //This is used for the file selector intent
        const val PICKFILE_REQUEST_CODE: Int = 10213
        const val ADD_PUBLISH_ACTIVITY_KEY = "ADD_PUBLISH_ACTIVITY_KEY"

        fun start(context: Context, basePublish: BasePublish? = null) {
            val intent = Intent(context, AddPublishActivity::class.java)

            basePublish?.let {
                if (basePublish is GeneralGooglePublish) {
                    intent.putExtra(ADD_PUBLISH_ACTIVITY_KEY, basePublish)
                }
            }

            context.startActivity(intent)
        }
    }
}
