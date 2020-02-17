package com.aconno.sensorics.ui.settings.publishers

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
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
class PublishListActivity : DaggerAppCompatActivity(), PublishListFragment.OnListFragmentClickListener,
        PublishListFragment.ItemSelectionStateListener {

    private var mainMenu: Menu? = null
    private var menuResource : Int = R.menu.share_all_menu

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



    override fun onItemSelectionStateEntered() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val cancelIcon = getDrawable(R.drawable.ic_action_notify_cancel)
        cancelIcon?.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
        supportActionBar?.setHomeAsUpIndicator(cancelIcon)

        menuResource = R.menu.share_selected_menu
        invalidateOptionsMenu()
    }

    override fun onItemSelectionStateExited() {
        custom_toolbar.title = getString(R.string.title_publish_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        menuResource = R.menu.share_all_menu
        invalidateOptionsMenu()
    }

    override fun onSelectedItemsCountChanged(selectedItems: Int) {
        custom_toolbar.title = getString(R.string.selected_items_count,selectedItems)
    }





    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        mainMenu = menu
        mainMenu?.clear()
        menuInflater.inflate(menuResource, menu)
        return true
    }

    override fun onBackPressed() {
        val publishListFragment = supportFragmentManager
                .findFragmentById(R.id.publish_list_container) as PublishListFragment

        val handled = publishListFragment.onBackButtonPressed()
        if(!handled) super.onBackPressed()
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
