package de.troido.acnsensa.tasker

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.twofortyfouram.locale.api.Intent.ACTION_QUERY_CONDITION
import com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE
import com.twofortyfouram.locale.sdk.client.internal.c
import com.twofortyfouram.spackle.AndroidSdkVersion
import com.twofortyfouram.spackle.bundle.BundleScrubber.scrub
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

abstract class EventReceiver : c() {
    fun a(result: Int) {
        if (result != 16 && result != 17 && result != 18) {
            throw AssertionError("Result=$result and not one of the following {16,17,18}")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (!scrub(intent)) {
            if (isOrderedBroadcast) {
                if (ACTION_QUERY_CONDITION != intent.action) {
                    resultCode = 18
                } else if (ComponentName(context, this.javaClass.name) != intent.component) {
                    resultCode = 18
                    abortBroadcast()
                } else {
                    val conditionDataBundle = intent.getBundleExtra(EXTRA_BUNDLE)
                    if (!scrub(intent)) {
                        if (conditionDataBundle == null) {
                            resultCode = 18
                        } else if (!this.isBundleValid(conditionDataBundle)) {
                            resultCode = 18
                        } else if (isAsync() && AndroidSdkVersion.isAtLeastSdk(11)) {
                            a({
                                a(getPluginConditionResult(context, conditionDataBundle, intent))
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }, isOrderedBroadcast)
                        } else {
                            getPluginConditionResult(context, conditionDataBundle, intent).let { result ->
                                a(result)
                                resultCode = result
                            }
                        }
                    }
                }
            }
        }
    }

    protected abstract fun isBundleValid(bundle: Bundle): Boolean

    protected abstract fun isAsync(): Boolean

    protected abstract fun getPluginConditionResult(context: Context, conditionBundle: Bundle, originalIntent: Intent): Int

    @Retention(RetentionPolicy.SOURCE)
    annotation class ConditionResult
}
