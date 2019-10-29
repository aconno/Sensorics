package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.AlarmService
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.dagger.action.ActionModule
import com.aconno.sensorics.dagger.action.ActionScope
import com.aconno.sensorics.dagger.actiondetails.ActionDetailsActivityScope
import com.aconno.sensorics.dagger.actiondetails.ActionDetailsModule
import com.aconno.sensorics.dagger.actionoutcome.ActionOutcomeModule
import com.aconno.sensorics.dagger.actionoutcome.ActionOutcomeScope
import com.aconno.sensorics.dagger.actionlist.ActionListFragmentsModule
import com.aconno.sensorics.dagger.actionlist.ActionListModule
import com.aconno.sensorics.dagger.actionlist.ActionListScope
import com.aconno.sensorics.dagger.alarm.AlarmServiceModule
import com.aconno.sensorics.dagger.alarm.AlarmServiceScope
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceModule
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceScope
import com.aconno.sensorics.dagger.configure.ConfigurationModule
import com.aconno.sensorics.dagger.configure.ConfigurationScope
import com.aconno.sensorics.dagger.device.DeviceModule
import com.aconno.sensorics.dagger.device.DeviceScope
import com.aconno.sensorics.dagger.deviceselect.DeviceSelectionFragmentsModule
import com.aconno.sensorics.dagger.dfu.DfuActivityScope
import com.aconno.sensorics.dagger.gcloudpublisher.GoogleCloudPublisherModule
import com.aconno.sensorics.dagger.gcloudpublisher.GoogleCloudPublisherScope
import com.aconno.sensorics.dagger.mainactivity.MainActivityFragmentsModule
import com.aconno.sensorics.dagger.mainactivity.MainActivityModule
import com.aconno.sensorics.dagger.mainactivity.MainActivityScope
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherModule
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherScope
import com.aconno.sensorics.dagger.publish.PublishModule
import com.aconno.sensorics.dagger.publish.PublishScope
import com.aconno.sensorics.dagger.publisher.PublisherModule
import com.aconno.sensorics.dagger.publisher.PublisherScope
import com.aconno.sensorics.dagger.publishlist.PublishListFragmentsModule
import com.aconno.sensorics.dagger.publishlist.PublishListModule
import com.aconno.sensorics.dagger.publishlist.PublishListScope
import com.aconno.sensorics.dagger.readings.SensorReadingsModule
import com.aconno.sensorics.dagger.readings.SensorReadingsScope
import com.aconno.sensorics.dagger.restpublisher.RESTPublisherModule
import com.aconno.sensorics.dagger.restpublisher.RESTPublisherScope
import com.aconno.sensorics.dagger.splash.SplashActivityModule
import com.aconno.sensorics.dagger.splash.SplashActivityScope
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.SplashActivity
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import com.aconno.sensorics.ui.configure.ConfigureActivity
import com.aconno.sensorics.ui.dfu.DfuActivity
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.GoogleCloudPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.MqttPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.RestPublisherActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @MainActivityScope
    @DeviceScope
    @SensorReadingsScope
    @ActionScope
    @ContributesAndroidInjector(
        modules = [MainActivityModule::class, MainActivityFragmentsModule::class,
        DeviceModule::class, SensorReadingsModule::class, ActionModule::class]
    )
    abstract fun bindMainActivity(): MainActivity

    @BluetoothScanningServiceScope
    @SensorReadingsScope
    @ActionOutcomeScope
    @PublishScope
    @ContributesAndroidInjector(modules = [BluetoothScanningServiceModule::class,
        SensorReadingsModule::class,ActionOutcomeModule::class, PublishModule::class])
    abstract fun bindBluetoothScanningService(): BluetoothScanningService

    @ContributesAndroidInjector
    abstract fun bindBluetoothConnectService(): BluetoothConnectService

    @PublishListScope
    @ContributesAndroidInjector(
        modules = [PublishListModule::class, PublishListFragmentsModule::class]
    )
    abstract fun bindPublishListActivity(): PublishListActivity

    @ActionListScope
    @ActionScope
    @DeviceScope
    @ContributesAndroidInjector(
        modules = [ActionListModule::class, ActionListFragmentsModule::class, DeviceModule::class,
            ActionModule::class]
    )
    abstract fun bindActionListActivity(): ActionListActivity

    @ActionDetailsActivityScope
    @ActionScope
    @ContributesAndroidInjector(modules = [ActionDetailsModule::class, ActionModule::class])
    abstract fun bindActionDetailsActivity(): ActionDetailsActivity

    @GoogleCloudPublisherScope
    @PublishScope
    @PublisherScope
    @ContributesAndroidInjector(
        modules = [GoogleCloudPublisherModule::class, DeviceSelectionFragmentsModule::class,
        PublishModule::class,PublisherModule::class]
    )
    abstract fun bindGoogleCloudPublisherActivity(): GoogleCloudPublisherActivity

    @MqttPublisherScope
    @PublishScope
    @PublisherScope
    @ContributesAndroidInjector(
        modules = [MqttPublisherModule::class, DeviceSelectionFragmentsModule::class,
            PublishModule::class,PublisherModule::class]
    )
    abstract fun bindMqttPublisherActivity(): MqttPublisherActivity

    @RESTPublisherScope
    @PublishScope
    @PublisherScope
    @ContributesAndroidInjector(
        modules = [RESTPublisherModule::class, DeviceSelectionFragmentsModule::class,
            PublishModule::class,PublisherModule::class]
    )
    abstract fun bindRestPublisherActivity(): RestPublisherActivity

    @SplashActivityScope
    @ContributesAndroidInjector(
        modules = [SplashActivityModule::class]
    )
    abstract fun bindSplashActivity(): SplashActivity

    @ConfigurationScope
    @ContributesAndroidInjector(modules = [ConfigurationModule::class])
    abstract fun bindConfigureActivity(): ConfigureActivity

    @DfuActivityScope
    @ContributesAndroidInjector
    abstract fun bindDfuActivity(): DfuActivity

    @AlarmServiceScope
    @ContributesAndroidInjector(modules = [AlarmServiceModule::class])
    abstract fun bindAlarmService(): AlarmService
}
