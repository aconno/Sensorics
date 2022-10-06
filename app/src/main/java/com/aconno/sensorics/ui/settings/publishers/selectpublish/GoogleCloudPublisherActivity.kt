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
import com.aconno.sensorics.databinding.ActivityGoogleCloudPublisherBinding
import com.aconno.sensorics.databinding.LayoutDatastringBinding
import com.aconno.sensorics.databinding.LayoutGoogleBinding
import com.aconno.sensorics.databinding.LayoutPublisherHeaderBinding
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.mapper.GooglePublishModelDataMapper
import com.aconno.sensorics.viewmodel.GoogleCloudPublisherViewModel
import com.aconno.sensorics.viewmodel.PublisherViewModel
//import kotlinx.android.synthetic.main.activity_google_cloud_publisher.*
//import kotlinx.android.synthetic.main.layout_datastring.*
//import kotlinx.android.synthetic.main.layout_google.*
//import kotlinx.android.synthetic.main.layout_publisher_header.*
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import javax.inject.Inject

class GoogleCloudPublisherActivity : BasePublisherActivity<GooglePublishModel>() {

    private lateinit var binding: ActivityGoogleCloudPublisherBinding
    private lateinit var layoutDatastringBinding: LayoutDatastringBinding
    private lateinit var layoutGoogleBinding: LayoutGoogleBinding
    private lateinit var layoutPublisherBinding: LayoutPublisherHeaderBinding

    @Inject
    lateinit var googleViewModel: GoogleCloudPublisherViewModel

    override val viewModel: PublisherViewModel<GooglePublishModel>
        get() = googleViewModel


    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityGoogleCloudPublisherBinding.inflate(layoutInflater)
        layoutDatastringBinding = LayoutDatastringBinding.inflate(layoutInflater)
        layoutGoogleBinding = LayoutGoogleBinding.inflate(layoutInflater)
        layoutPublisherBinding = LayoutPublisherHeaderBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.customToolbar)
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
                    layoutGoogleBinding.editPrivatekey.text = path
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

        layoutGoogleBinding.editPrivatekey.setOnClickListener {

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

        layoutGoogleBinding.editProjectid.setText(model.projectId)
        layoutGoogleBinding.editRegion.setText(model.region)
        layoutGoogleBinding.editDeviceregistry.setText(model.deviceRegistry)
        layoutGoogleBinding.editDevice.setText(model.device)
        layoutGoogleBinding.editPrivatekey.text = model.privateKey
    }

    override fun toPublishModel(): GooglePublishModel? {
        val name = layoutPublisherBinding.editName.text.toString().trim()
        val projectId = layoutGoogleBinding.editProjectid.text.toString().trim()
        val region = layoutGoogleBinding.editRegion.text.toString().trim()
        val deviceRegistry = layoutGoogleBinding.editDeviceregistry.text.toString().trim()
        val device = layoutGoogleBinding.editDevice.text.toString().trim()
        val privateKey = layoutGoogleBinding.editPrivatekey.text.toString().trim()
        val timeType = layoutPublisherBinding.spinnerIntervalTime.selectedItem.toString()
        val timeCount = layoutPublisherBinding.editIntervalCount.text.toString()
        val datastring = layoutDatastringBinding.editDatastring.text.toString()

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
