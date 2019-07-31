package com.aconno.sensorics.ui.settings.publishers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.aconno.sensorics.R
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.RestPublishModel
import com.aconno.sensorics.ui.settings.publishers.selectpublish.GoogleCloudPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.MqttPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.RestPublisherActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_publish_list.*


/**
 * @author aconno
 */
class PublishListActivity : DaggerAppCompatActivity(), PublishListFragment.OnListFragmentClickListener {

    private var mainMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_list)

        supportFragmentManager.findFragmentById(
            R.id.publish_list_container
        ) ?: PublishListFragment.newInstance().also {
            supportFragmentManager.beginTransaction()
                .add(R.id.publish_list_container, it)
                .commit()
        }

        invalidateOptionsMenu()

        setSupportActionBar(custom_toolbar)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        mainMenu = menu
        mainMenu?.clear()
        menuInflater.inflate(R.menu.share_all_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        (supportFragmentManager
            .findFragmentById(R.id.publish_list_container) as PublishListFragment)
            .resolveActionBarEvent(item)

        return super.onOptionsItemSelected(item)
    }

    override fun onListFragmentClick(item: BasePublishModel?) {
        when (item) {
            is GooglePublishModel -> GoogleCloudPublisherActivity.start(this, item)
            is RestPublishModel -> RestPublisherActivity.start(this, item)
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
