package com.aconno.acnsensa.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.view.MenuItem
import com.aconno.acnsensa.R
import android.preference.Preference
import android.preference.PreferenceManager
import android.text.TextUtils
import android.widget.Toast


class SettingsActivity : AppCompatPreferenceActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || PreferencesGoogleCloud::class.java.name == fragmentName
    }

    /**
     * Populate the activity with the top-level headers.
     */
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.preference_headers, target)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }
    }

    /**
     * This class is used to launch Google Cloud settings from Google Cloud Header
     */
    class PreferencesGoogleCloud : PreferenceFragment() {

        companion object {
            //This is used for the file selector intent
            private const val PICKFILE_REQUEST_CODE: Int = 10213
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences_gcloud)

            // Load and set file picker intent to Private Key File Preference
            val filePicker = findPreference("privatekey_preference") as Preference
            filePicker.setOnPreferenceClickListener {

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                startActivityForResult(intent, PICKFILE_REQUEST_CODE)
                true
            }

            val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
            defaultSharedPreferences?.let {
                val privateKeyPath = defaultSharedPreferences.getString("privatekey_preference", "")
                if (!TextUtils.isEmpty(privateKeyPath)) {
                    filePicker.summary = privateKeyPath
                }
            }
        }

        /**
         * This method is called after @Intent.ACTION_GET_CONTENT result is returned.
         */
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == Activity.RESULT_OK && requestCode == PICKFILE_REQUEST_CODE) {
                data?.let {
                    val path = it.dataString
                    val editor = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext).edit()
                    editor.putString("privatekey_preference", path)
                    editor.apply()

                    val filePicker = findPreference("privatekey_preference") as Preference
                    filePicker.summary = path

                    Toast.makeText(activity, path, Toast.LENGTH_SHORT).show()
                }

            }

            super.onActivityResult(requestCode, resultCode, data)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }
}