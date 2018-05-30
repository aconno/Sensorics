package com.aconno.acnsensa.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.R.id.*
import com.aconno.acnsensa.R.string.device
import com.aconno.acnsensa.R.string.region
import com.aconno.acnsensa.dagger.addpublish.AddPublishComponent
import com.aconno.acnsensa.dagger.addpublish.AddPublishModule
import com.aconno.acnsensa.dagger.addpublish.DaggerAddPublishComponent
import com.aconno.acnsensa.domain.ifttt.*
import com.aconno.acnsensa.model.BasePublishModel
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.RESTPublishModel
import com.aconno.acnsensa.viewmodel.PublishViewModel
import kotlinx.android.synthetic.main.activity_add_publish.*
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddPublishActivity : AppCompatActivity() {


    @Inject
    lateinit var publishViewModel: PublishViewModel

    private var basePublish: BasePublishModel? = null

    private val addPublishComponent: AddPublishComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication

        DaggerAddPublishComponent.builder().appComponent(acnSensaApplication?.appComponent)
            .addPublishModule(AddPublishModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_publish)
        addPublishComponent.inject(this)
        val temp = intent.getParcelableExtra<BasePublishModel>(ADD_PUBLISH_ACTIVITY_KEY)
        setSupportActionBar(custom_toolbar)

        when {
            temp is BasePublishModel -> {
                basePublish = temp
                setTextsWithTemp()
            }
            temp != null -> throw IllegalArgumentException("Only classes that extend from BasePublish can be sent")
        }

        initViews()
    }

    private fun initViews() {
        edit_privatekey.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
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
                    0 -> {
                        layout_google.visibility = View.VISIBLE
                        layout_rest.visibility = View.GONE
                    }
                    1 -> {
                        layout_rest.visibility = View.VISIBLE
                        layout_google.visibility = View.GONE
                    }
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
                val path = it.data.toString()

                if (isFileValidPKCS8(getPrivateKeyData(it.data.toString()))) {
                    edit_privatekey.text = path
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.not_valid_file_pkcs8),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setTextsWithTemp() {
        edit_name.setText(basePublish?.name)
        spinner_interval_time.setSelection(
            resources.getStringArray(R.array.PublishIntervals).indexOf(
                basePublish?.timeType
            )
        )

        edit_interval_count.setText(
            calculateCountFromMillis(
                basePublish!!.timeMillis,
                basePublish!!.timeType
            )
        )

        val str = getString(R.string.last_data_sent) + " " +
                millisToFormattedDateString(
                    basePublish!!.lastTimeMillis
                )
        text_lastdatasent.text = str

        if (basePublish is GooglePublishModel) {
            spinner.setSelection(0)
            layout_google.visibility = View.VISIBLE

            val googlePublish = basePublish as GooglePublish

            edit_projectid.setText(googlePublish.projectId)
            edit_region.setText(googlePublish.region)
            edit_deviceregistry.setText(googlePublish.deviceRegistry)
            edit_device.setText(googlePublish.device)
            edit_privatekey.text = googlePublish.privateKey
        } else if (basePublish is RESTPublishModel) {
            spinner.setSelection(1)
            layout_rest.visibility = View.VISIBLE

            val restPublish = basePublish as RESTPublish

            edit_url.setText(restPublish.url)
            val selection = if (restPublish.method == "GET") 0 else 1
            spinner_methods.setSelection(selection)
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
            R.id.action_publish_done -> addOrUpdate()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun addOrUpdate() {
        val selectedItem = spinner.selectedItemPosition

        when (selectedItem) {
            0 -> googleAddOrUpdate()
            1 -> restAddOrUpdate()
            else -> throw IllegalArgumentException("Please use registered types.")
        }

        //After save or update finish activity
        finish()
    }

    private fun millisToFormattedDateString(millis: Long): String {

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS", Locale.US)
        val date = Date(millis)

        return sdf.format(date)
    }

    private fun restAddOrUpdate() {
        val toastText: String

        val restPublishModel = toRESTPublishModel()
        if (restPublishModel != null) {

            if (basePublish != null) {
                toastText = "Updated"
                publishViewModel.update(restPublishModel)
            } else {
                toastText = "Created"
                publishViewModel.save(restPublishModel)
            }

            Toast.makeText(this, "$toastText $name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toRESTPublishModel(): RESTPublishModel? {
        val name = edit_name.text.toString().trim()
        val url = edit_url.text.toString().trim()
        val method = spinner_methods.selectedItem.toString()
        val timeType = spinner_interval_time.selectedItem.toString()
        val timeCount = edit_interval_count.text.toString()

        if (publishViewModel.checkFieldsAreEmpty(
                name,
                url,
                method,
                timeType,
                timeCount
            )
        ) {
            Toast.makeText(this, "Please fill the blanks", Toast.LENGTH_SHORT).show()
            return null
        }

        val id = if (basePublish == null) 0 else basePublish!!.id
        val timeMillis = if (basePublish == null) 0 else calculateMillis(timeCount, timeType)
        val lastTimeMillis = if (basePublish == null) 0 else basePublish!!.lastTimeMillis
        return RESTPublishModel(
            id,
            name,
            url,
            method,
            false,
            timeType,
            timeMillis,
            lastTimeMillis
        )
    }


    private fun calculateMillis(timeCount: String, timeType: String): Long {
        return when (timeType) {
            getString(R.string.publish_sec) -> TimeUnit.SECONDS.toMillis(timeCount.toLong())
            getString(R.string.publish_min) -> TimeUnit.MINUTES.toMillis(timeCount.toLong())
            getString(R.string.publish_hour) -> TimeUnit.HOURS.toMillis(timeCount.toLong())
            getString(R.string.publish_day) -> TimeUnit.DAYS.toMillis(timeCount.toLong())
            else -> throw IllegalArgumentException("Illegal Publish Time Type Provided.")
        }
    }

    private fun calculateCountFromMillis(timeMillis: Long, timeType: String): String {
        return when (timeType) {
            getString(R.string.publish_sec) -> TimeUnit.MILLISECONDS.toSeconds(timeMillis).toString()
            getString(R.string.publish_min) -> TimeUnit.MILLISECONDS.toMinutes(timeMillis).toString()
            getString(R.string.publish_hour) -> TimeUnit.MILLISECONDS.toHours(timeMillis).toString()
            getString(R.string.publish_day) -> TimeUnit.MILLISECONDS.toDays(timeMillis).toString()
            else -> throw IllegalArgumentException("Illegal Publish Time Type Provided.")
        }
    }

    private fun googleAddOrUpdate() {
        val toastText: String

        val googlePublishModel = toGooglePublishModel()
        if (googlePublishModel != null) {

            if (basePublish != null) {
                toastText = "Updated"
                publishViewModel.update(googlePublishModel)
            } else {
                toastText = "Created"
                publishViewModel.update(googlePublishModel)
            }

            Toast.makeText(this, "$toastText $name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toGooglePublishModel(): GooglePublishModel? {
        val name = edit_name.text.toString().trim()
        val projectId = edit_projectid.text.toString().trim()
        val region = edit_region.text.toString().trim()
        val deviceRegistry = edit_deviceregistry.text.toString().trim()
        val device = edit_device.text.toString().trim()
        val privateKey = edit_privatekey.text.toString().trim()
        val timeType = spinner_interval_time.selectedItem.toString()
        val timeCount = edit_interval_count.text.toString()

        if (publishViewModel.checkFieldsAreEmpty(
                name,
                projectId,
                region,
                deviceRegistry,
                device,
                privateKey,
                timeType,
                timeCount
            )
        ) {
            Toast.makeText(this, "Please fill the blanks", Toast.LENGTH_SHORT).show()
            return null
        }

        val id = if (basePublish == null) 0 else basePublish!!.id
        val timeMillis = if (basePublish == null) 0 else calculateMillis(timeCount, timeType)
        val lastTimeMillis = if (basePublish == null) 0 else basePublish!!.lastTimeMillis
        return GooglePublishModel(
            id,
            name,
            projectId,
            region,
            deviceRegistry,
            device,
            privateKey,
            false,
            timeType,
            timeMillis,
            lastTimeMillis
        )
    }


    private fun isFileValidPKCS8(byteArray: ByteArray): Boolean {
        val spec = PKCS8EncodedKeySpec(byteArray)
        val kf = KeyFactory.getInstance("RSA")

        try {
            kf.generatePrivate(spec)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun getPrivateKeyData(privateKey: String): ByteArray {
        val uri = Uri.parse(privateKey)
        val stream = contentResolver.openInputStream(uri)

        val size = stream.available()
        val buffer = ByteArray(size)
        stream.read(buffer)
        stream.close()
        return buffer
    }

    companion object {
        //This is used for the file selector intent
        const val PICKFILE_REQUEST_CODE: Int = 10213
        const val ADD_PUBLISH_ACTIVITY_KEY = "ADD_PUBLISH_ACTIVITY_KEY"

        fun start(context: Context, basePublish: BasePublishModel? = null) {
            val intent = Intent(context, AddPublishActivity::class.java)

            basePublish?.let {
                intent.putExtra(
                    ADD_PUBLISH_ACTIVITY_KEY,
                    basePublish
                )

//                when (basePublish) {
//                    is GooglePublishModel -> intent.putExtra(
//                        ADD_PUBLISH_ACTIVITY_KEY,
//                        basePublish
//                    )
//                    is RESTPublishModel -> intent.putExtra(ADD_PUBLISH_ACTIVITY_KEY, basePublish)
//                    else -> throw IllegalArgumentException("Illegal Publish type provided")
//                }
            }

            context.startActivity(intent)
        }
    }
}
