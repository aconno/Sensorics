package com.aconno.sensorics.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.aconno.sensorics.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_action_list.*

class ActionListActivity : DaggerAppCompatActivity(),
    ActionListFragment.ItemSelectionStateListener {

    private var mainMenu: Menu? = null
    private var menuResource: Int = R.menu.share_all_menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_list)
        actions_toolbar.title = getString(R.string.actions)

        val fragment: Fragment? = supportFragmentManager.findFragmentById(
            R.id.action_list_container
        )
        if (fragment == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.action_list_container,
                ActionListFragment.newInstance()
            ).commit()
        }

        invalidateOptionsMenu()

        setSupportActionBar(actions_toolbar)
    }


    override fun onItemSelectionStateEntered() {
        supportActionBar?.let { actionBar ->
            actionBar.setDisplayHomeAsUpEnabled(true)

            getDrawable(R.drawable.ic_action_notify_cancel)?.let { drawable ->
                drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY) // TODO: Fix
                actionBar.setHomeAsUpIndicator(drawable)
            }
        }

        menuResource = R.menu.selected_shareable_items_menu
        invalidateOptionsMenu()
    }

    override fun onItemSelectionStateExited() {
        actions_toolbar.title = getString(R.string.actions)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        menuResource = R.menu.share_all_menu
        invalidateOptionsMenu()
    }

    override fun onSelectedItemsCountChanged(selectedItems: Int) {
        actions_toolbar.title = getString(R.string.selected_items_count, selectedItems)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        mainMenu = menu
        mainMenu?.clear()
        menuInflater.inflate(menuResource, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        (supportFragmentManager
            .findFragmentById(R.id.action_list_container) as ActionListFragment)
            .resolveActionBarEvent(item)

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val actionListFragment = supportFragmentManager
            .findFragmentById(R.id.action_list_container) as ActionListFragment

        val handled = actionListFragment.onBackButtonPressed()
        if (!handled) super.onBackPressed()
    }


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ActionListActivity::class.java)
            context.startActivity(intent)
        }
    }
}
