package com.aconno.acnsensa.ui.settings.publishers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.aconno.acnsensa.R
import com.aconno.acnsensa.model.BasePublishModel
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.MqttPublishModel
import com.aconno.acnsensa.model.RESTPublishModel
import com.aconno.acnsensa.ui.settings.publishers.selectpublish.GoogleCloudPublisherActivity
import com.aconno.acnsensa.ui.settings.publishers.selectpublish.MqttPublisherActivity
import com.aconno.acnsensa.ui.settings.publishers.selectpublish.RESTPublisherActivity
import kotlinx.android.synthetic.main.activity_add_publish.*


/**
 * @author aconno
 */
class PublishListActivity : AppCompatActivity(),
    PublishListFragment.OnListFragmentInteractionListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_list)

        val fm = supportFragmentManager
        if (fm != null) {
            var fragment: Fragment? = fm.findFragmentById(R.id.publish_list_container)
            if (fragment == null) {
                fragment =
                        PublishListFragment.newInstance()
                fm.beginTransaction().add(R.id.publish_list_container, fragment).commit()
            }
        }

        setSupportActionBar(custom_toolbar)
    }

    override fun onListFragmentInteraction(item: BasePublishModel?) {
        when (item) {
            is GooglePublishModel -> GoogleCloudPublisherActivity.start(this, item)
            is RESTPublishModel -> RESTPublisherActivity.start(this, item)
            is MqttPublishModel -> MqttPublisherActivity.start(this, item)
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PublishListActivity::class.java)
            context.startActivity(intent)
        }
    }
}
