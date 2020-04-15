package com.aconno.sensorics.service

import android.app.Activity
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.ui.dfu.DfuActivity
import no.nordicsemi.android.dfu.DfuBaseService

class DfuService : DfuBaseService() {

    /* As a target activity the NotificationActivity is returned, not the MainActivity. This is because
    * the notification must create a new task:
    *
    * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    *
    * when you press it. You can use NotificationActivity to check whether the new activity
    * is a root activity (that means no other activity was open earlier) or that some
    * other activity is already open. In the latter case the NotificationActivity will just be
    * closed. The system will restore the previous activity. However, if the application has been
    * closed during upload and you click the notification, a NotificationActivity will
    * be launched as a root activity. It will create and start the main activity and
    * terminate itself.
    *
    * This method may be used to restore the target activity in case the application
    * was closed or is open. It may also be used to recreate an activity history using
    * startActivities(...).
    */
    override fun getNotificationTarget(): Class<out Activity>? {
        return DfuActivity::class.java
    }

    override fun isDebug(): Boolean {
        // Here return true if you want the service to print more logs in LogCat.
        // Library's BuildConfig in current version of Android Studio is always set to DEBUG=false, so
        // make sure you return true or your.app.BuildConfig.DEBUG here.
        return BuildConfig.DEBUG
    }
}
