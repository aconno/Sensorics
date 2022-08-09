package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.aconno.sensorics.PublisherIntervalConverter
import com.aconno.sensorics.R
import com.aconno.sensorics.data.publisher.GoogleCloudPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.mapper.GooglePublishModelDataMapper
import com.aconno.sensorics.viewmodel.GoogleCloudPublisherViewModel
import com.aconno.sensorics.viewmodel.PublisherViewModel
import kotlinx.android.synthetic.main.activity_google_cloud_publisher.*
import kotlinx.android.synthetic.main.layout_datastring.*
import kotlinx.android.synthetic.main.layout_google.*
import kotlinx.android.synthetic.main.layout_publisher_header.*
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import javax.inject.Inject

class GoogleCloudPublisherActivity : BasePublisherActivity<GooglePublishModel>() {

    @Inject
    lateinit var googleViewModel: GoogleCloudPublisherViewModel

    override val viewModel: PublisherViewModel<GooglePublishModel>
        get() = googleViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_google_cloud_publisher)

        setSupportActionBar(custom_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        super.onCreate(savedInstanceState)
    }

    /**
     * This method is called after @Intent.ACTION_OPEN_DOCUMENT result is returned.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICKFILE_REQUEST_CODE) {
            data?.data?.let {
                val path = it.toString()

                applicationContext.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                if (isFileValidPKCS8(getPrivateKeyData(path))) {
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

    override fun initViews() {
        super.initViews()

        edit_privatekey.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            startActivityForResult(
                intent,
                PICKFILE_REQUEST_CODE
            )

        }
    }


    override fun setFields(model: GooglePublishModel) {
        super.setFields(model)

        edit_projectid.setText(model.projectId)
        edit_region.setText(model.region)
        edit_deviceregistry.setText(model.deviceRegistry)
        edit_device.setText(model.device)
        edit_privatekey.text = model.privateKey
    }

    override fun toPublishModel(): GooglePublishModel? {
        val name = edit_name.text.toString().trim()
        val projectId = edit_projectid.text.toString().trim()
        val region = edit_region.text.toString().trim()
        val deviceRegistry = edit_deviceregistry.text.toString().trim()
        val device = edit_device.text.toString().trim()
        val privateKey = edit_privatekey.text.toString().trim()
        val timeType = spinner_interval_time.selectedItem.toString()
        val timeCount = edit_interval_count.text.toString()
        val datastring = edit_datastring.text.toString()

        if (viewModel.checkFieldsAreEmpty(
                name,
                projectId,
                region,
                deviceRegistry,
                device,
                privateKey,
                timeType,
                timeCount,
                datastring
            )
        ) {
            Toast.makeText(this, getString(R.string.please_fill_blanks), Toast.LENGTH_SHORT).show()
            return null
        } else {
            if (!isDataStringValid()) {
                Toast.makeText(
                        this,
                        getString(R.string.data_string_not_valid),
                        Toast.LENGTH_SHORT
                    )
                    .show()

                return null
            }
        }

        val id = model?.id ?: 0
        val timeMillis = PublisherIntervalConverter.calculateMillis(this, timeCount, timeType)
        val lastTimeMillis = model?.lastTimeMillis ?: 0
        return GooglePublishModel(
            id,
            name,
            projectId,
            region,
            deviceRegistry,
            device,
            privateKey,
            model?.enabled ?: true,
            timeType,
            timeMillis,
            lastTimeMillis,
            datastring
        )
    }

    private fun isFileValidPKCS8(byteArray: ByteArray): Boolean {
        val spec = PKCS8EncodedKeySpec(byteArray)
        val keyFactory = KeyFactory.getInstance("RSA")

        try {
            keyFactory.generatePrivate(spec)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun getPrivateKeyData(privateKey: String): ByteArray {
        return contentResolver.openInputStream(Uri.parse(privateKey))?.use { stream ->
            stream.readBytes()
        } ?: byteArrayOf()
    }

    override fun getPublisherForModel(model: GooglePublishModel): Publisher<*> {
        return GoogleCloudPublisher(
            applicationContext,
            GooglePublishModelDataMapper().transform(model),
            listOf(Device("TestDevice", "Name", "Mac")),
            syncRepository
        )
    }

    companion object {
        //This is used for the file selector intent
        const val PICKFILE_REQUEST_CODE: Int = 10213

        fun start(context: Context, id: Long? = null) {
            val intent = Intent(context, GoogleCloudPublisherActivity::class.java)

            id?.let {
                intent.putExtra(
                    PUBLISHER_ID_KEY,
                    id
                )
            }

            context.startActivity(intent)
        }
    }
}
