package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.dagger.action_details.ActionDetailsActivityScope
import com.aconno.sensorics.dagger.action_details.ActionDetailsModule
import com.aconno.sensorics.dagger.actionlist.ActionListFragmentsModule
import com.aconno.sensorics.dagger.actionlist.ActionListModule
import com.aconno.sensorics.dagger.actionlist.ActionListScope
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceModule
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceScope
import com.aconno.sensorics.dagger.deviceselect.DeviceSelectionFragmentsModule
import com.aconno.sensorics.dagger.gcloudpublisher.GoogleCloudPublisherModule
import com.aconno.sensorics.dagger.gcloudpublisher.GoogleCloudPublisherScope
import com.aconno.sensorics.dagger.mainactivity.MainActivityFragmentsModule
import com.aconno.sensorics.dagger.mainactivity.MainActivityModule
import com.aconno.sensorics.dagger.mainactivity.MainActivityScope
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherModule
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherScope
import com.aconno.sensorics.dagger.publish.PublishListFragmentsModule
import com.aconno.sensorics.dagger.publish.PublishListModule
import com.aconno.sensorics.dagger.publish.PublishListScope
import com.aconno.sensorics.dagger.restpublisher.RESTPublisherModule
import com.aconno.sensorics.dagger.restpublisher.RESTPublisherScope
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.SplashActivity
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import com.aconno.sensorics.ui.devicecon.AcnFreightFragment
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.GoogleCloudPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.MqttPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.RestPublisherActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector
    abstract fun bindAcnFreightFragment(): AcnFreightFragment

    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @MainActivityScope
    @ContributesAndroidInjector(
        modules = [MainActivityModule::class, MainActivityFragmentsModule::class]
    )
    abstract fun bindMainActivity(): MainActivity

    @BluetoothScanningServiceScope
    @ContributesAndroidInjector(modules = [BluetoothScanningServiceModule::class])
    abstract fun bindBluetoothScanningService(): BluetoothScanningService

    @ContributesAndroidInjector
    abstract fun bindBluetoothConnectService(): BluetoothConnectService

    @PublishListScope
    @ContributesAndroidInjector(
        modules = [PublishListModule::class, PublishListFragmentsModule::class]
    )
    abstract fun bindPublishListActivity(): PublishListActivity

    @ActionListScope
    @ContributesAndroidInjector(
        modules = [ActionListModule::class, ActionListFragmentsModule::class]
    )
    abstract fun bindActionListActivity(): ActionListActivity

    @ActionDetailsActivityScope
    @ContributesAndroidInjector(modules = [ActionDetailsModule::class])
    abstract fun bindActionDetailsActivity(): ActionDetailsActivity

    @GoogleCloudPublisherScope
    @ContributesAndroidInjector(
        modules = [GoogleCloudPublisherModule::class, DeviceSelectionFragmentsModule::class]
    )
    abstract fun bindGoogleCloudPublisherActivity(): GoogleCloudPublisherActivity

    @MqttPublisherScope
    @ContributesAndroidInjector(
        modules = [MqttPublisherModule::class, DeviceSelectionFragmentsModule::class]
    )
    abstract fun bindMqttPublisherActivity(): MqttPublisherActivity

    @RESTPublisherScope
    @ContributesAndroidInjector(
        modules = [RESTPublisherModule::class, DeviceSelectionFragmentsModule::class]
    )
    abstract fun bindRestPublisherActivity(): RestPublisherActivity
}
