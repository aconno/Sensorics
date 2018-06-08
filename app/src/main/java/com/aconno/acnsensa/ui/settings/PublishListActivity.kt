package com.aconno.acnsensa.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.aconno.acnsensa.R
import com.aconno.acnsensa.model.BasePublishModel
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
                fragment = PublishListFragment.newInstance()
                fm.beginTransaction().add(R.id.publish_list_container, fragment).commit()
            }
        }

        setSupportActionBar(custom_toolbar)
    }

    override fun onListFragmentInteraction(item: BasePublishModel?) {
        AddPublishActivity.start(this, item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.publish_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
        //TODO Add startActivityForResult
            R.id.action_publish_add -> AddPublishActivity.start(
                this
            )
        }

        return super.onOptionsItemSelected(item)
    }


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PublishListActivity::class.java)
            context.startActivity(intent)
        }
    }
}
