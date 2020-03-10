package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.AlarmService
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.MqttVirtualScanningService
import com.aconno.sensorics.dagger.action.ActionModule
import com.aconno.sensorics.dagger.action.ActionScope
import com.aconno.sensorics.dagger.actiondetails.ActionDetailsActivityModule
import com.aconno.sensorics.dagger.actiondetails.ActionDetailsActivityScope
import com.aconno.sensorics.dagger.actionlist.ActionListActivityModule
import com.aconno.sensorics.dagger.actionlist.ActionListActivityScope
import com.aconno.sensorics.dagger.actionlist.ActionListFragmentsModule
import com.aconno.sensorics.dagger.actionoutcome.ActionOutcomeModule
import com.aconno.sensorics.dagger.actionoutcome.ActionOutcomeScope
import com.aconno.sensorics.dagger.alarm.AlarmServiceModule
import com.aconno.sensorics.dagger.alarm.AlarmServiceScope
import com.aconno.sensorics.dagger.azuremqttpublisher.AzureMqttPublisherActivityModule
import com.aconno.sensorics.dagger.azuremqttpublisher.AzureMqttPublisherActivityScope
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanResultsModule
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanResultsScope
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceModule
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceScope
import com.aconno.sensorics.dagger.compositescan.CompositeScanResultsScope
import com.aconno.sensorics.dagger.configure.ConfigureActivityModule
import com.aconno.sensorics.dagger.configure.ConfigureActivityScope
import com.aconno.sensorics.dagger.device.DeviceModule
import com.aconno.sensorics.dagger.device.DeviceScope
import com.aconno.sensorics.dagger.deviceselect.DeviceSelectionFragmentsModule
import com.aconno.sensorics.dagger.dfu.DfuActivityScope
import com.aconno.sensorics.dagger.gcloudpublisher.GoogleCloudPublisherActivityModule
import com.aconno.sensorics.dagger.gcloudpublisher.GoogleCloudPublisherActivityScope
import com.aconno.sensorics.dagger.mainactivity.MainActivityFragmentsModule
import com.aconno.sensorics.dagger.mainactivity.MainActivityModule
import com.aconno.sensorics.dagger.mainactivity.MainActivityScope
import com.aconno.sensorics.dagger.mqtt.MqttScanResultsModule
import com.aconno.sensorics.dagger.mqtt.MqttScanResultsScope
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherActivityModule
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherActivityScope
import com.aconno.sensorics.dagger.mqttvirtualscanningsource.MqttVirtualScanningSourceActivityModule
import com.aconno.sensorics.dagger.mqttvirtualscanningsource.MqttVirtualScanningSourceActivityScope
import com.aconno.sensorics.dagger.mqttvirtualscanning.MqttVirtualScanningServiceModule
import com.aconno.sensorics.dagger.mqttvirtualscanning.MqttVirtualScanningServiceScope
import com.aconno.sensorics.dagger.publisher.PublisherModule
import com.aconno.sensorics.dagger.publisher.PublisherScope
import com.aconno.sensorics.dagger.publishlist.PublishListActivityModule
import com.aconno.sensorics.dagger.publishlist.PublishListActivityScope
import com.aconno.sensorics.dagger.publishlist.PublishListFragmentsModule
import com.aconno.sensorics.dagger.readings.SensorReadingsModule
import com.aconno.sensorics.dagger.readings.SensorReadingsScope
import com.aconno.sensorics.dagger.restpublisher.RESTPublisherActivityModule
import com.aconno.sensorics.dagger.restpublisher.RESTPublisherActivityScope
import com.aconno.sensorics.dagger.settings_framework.FragmentsModule
import com.aconno.sensorics.dagger.settings_framework.BeaconSettingsActivityModule
import com.aconno.sensorics.dagger.settings_framework.BeaconSettingsActivityScope
import com.aconno.sensorics.dagger.splash.SplashActivityModule
import com.aconno.sensorics.dagger.splash.SplashActivityScope
import com.aconno.sensorics.dagger.virtualscanningsource.VirtualScanningSourceModule
import com.aconno.sensorics.dagger.virtualscanningsource.VirtualScanningSourceScope
import com.aconno.sensorics.dagger.virtualscanningsourcelist.VirtualScanningSourceListActivityFragmentsModule
import com.aconno.sensorics.dagger.virtualscanningsourcelist.VirtualScanningSourceListActivityModule
import com.aconno.sensorics.dagger.virtualscanningsourcelist.VirtualScanningSourceListActivityScope
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.SplashActivity
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import com.aconno.sensorics.ui.dfu.DfuActivity
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.AzureMqttPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.GoogleCloudPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.MqttPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.RestPublisherActivity
import com.aconno.sensorics.ui.settings_framework.BeaconSettingsActivity
import com.aconno.sensorics.ui.settings.virtualscanningsources.MqttVirtualScanningSourceActivity
import com.aconno.sensorics.ui.settings.virtualscanningsources.VirtualScanningSourceListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @MainActivityScope
    @DeviceScope
    @SensorReadingsScope
    @CompositeScanResultsScope
    @ActionScope
    @ContributesAndroidInjector(
        modules = [
            MainActivityModule::class, MainActivityFragmentsModule::class,
            DeviceModule::class, SensorReadingsModule::class, ActionModule::class
        ]
    )
    abstract fun bindMainActivity(): MainActivity

    @BluetoothScanningServiceScope
    @BluetoothScanResultsScope
    @SensorReadingsScope
    @ActionOutcomeScope
    @PublisherScope
    @ActionScope
    @DeviceScope
    @ContributesAndroidInjector(
        modules = [
            BluetoothScanningServiceModule::class, BluetoothScanResultsModule::class,
            SensorReadingsModule::class, ActionOutcomeModule::class, PublisherModule::class,
            ActionModule::class, DeviceModule::class
        ]
    )
    abstract fun bindBluetoothScanningService(): BluetoothScanningService

    @MqttVirtualScanningServiceScope
    @MqttScanResultsScope
    @DeviceScope
    @ActionScope
    @PublisherScope
    @SensorReadingsScope
    @ActionOutcomeScope
    @VirtualScanningSourceScope
    @ContributesAndroidInjector(
        modules = [
            MqttVirtualScanningServiceModule::class, MqttScanResultsModule::class,
            DeviceModule::class, ActionModule::class,
            PublisherModule::class, SensorReadingsModule::class,
            ActionOutcomeModule::class, VirtualScanningSourceModule::class
        ]
    )
    abstract fun bindMqttVirtualScanningService(): MqttVirtualScanningService

    @ContributesAndroidInjector
    abstract fun bindBluetoothConnectService(): BluetoothConnectService

    @PublishListActivityScope
    @PublisherScope
    @ContributesAndroidInjector(
        modules = [PublishListActivityModule::class, PublishListFragmentsModule::class,
            PublisherModule::class
        ]
    )
    abstract fun bindPublishListActivity(): PublishListActivity

    @VirtualScanningSourceListActivityScope
    @VirtualScanningSourceScope
    @ContributesAndroidInjector(
        modules = [VirtualScanningSourceListActivityModule::class, VirtualScanningSourceListActivityFragmentsModule::class,
            VirtualScanningSourceModule::class
        ]
    )
    abstract fun bindVirtualScanningSourceListActivity(): VirtualScanningSourceListActivity

    @ActionListActivityScope
    @ActionScope
    @DeviceScope
    @ContributesAndroidInjector(
        modules = [ActionListActivityModule::class, ActionListFragmentsModule::class,
            DeviceModule::class, ActionModule::class
        ]
    )
    abstract fun bindActionListActivity(): ActionListActivity

    @ActionDetailsActivityScope
    @ActionScope
    @DeviceScope
    @ContributesAndroidInjector(
        modules = [ActionDetailsActivityModule::class,
            ActionModule::class, DeviceModule::class
        ]
    )
    abstract fun bindActionDetailsActivity(): ActionDetailsActivity

    @GoogleCloudPublisherActivityScope
    @PublisherScope
    @DeviceScope
    @ContributesAndroidInjector(
        modules = [GoogleCloudPublisherActivityModule::class, DeviceSelectionFragmentsModule::class,
            PublisherModule::class, DeviceModule::class
        ]
    )
    abstract fun bindGoogleCloudPublisherActivity(): GoogleCloudPublisherActivity

    @MqttPublisherActivityScope
    @PublisherScope
    @DeviceScope
    @ContributesAndroidInjector(
        modules = [MqttPublisherActivityModule::class, DeviceSelectionFragmentsModule::class,
            PublisherModule::class, DeviceModule::class
        ]
    )
    abstract fun bindMqttPublisherActivity(): MqttPublisherActivity

    @MqttVirtualScanningSourceActivityScope
    @VirtualScanningSourceScope
    @ContributesAndroidInjector(
        modules = [MqttVirtualScanningSourceActivityModule::class,
            VirtualScanningSourceModule::class
        ]
    )
    abstract fun bindMqttVirtualScanningSourceActivity(): MqttVirtualScanningSourceActivity

    @AzureMqttPublisherActivityScope
    @DeviceScope
    @PublisherScope
    @ContributesAndroidInjector(
        modules = [AzureMqttPublisherActivityModule::class, DeviceSelectionFragmentsModule::class,
            PublisherModule::class, DeviceModule::class
        ]
    )
    abstract fun bindAzureMqttPublisherActivity(): AzureMqttPublisherActivity

    @RESTPublisherActivityScope
    @PublisherScope
    @DeviceScope
    @ContributesAndroidInjector(
        modules = [RESTPublisherActivityModule::class, DeviceSelectionFragmentsModule::class,
            PublisherModule::class, DeviceModule::class
        ]
    )
    abstract fun bindRestPublisherActivity(): RestPublisherActivity

    @SplashActivityScope
    @ContributesAndroidInjector(
        modules = [SplashActivityModule::class]
    )
    abstract fun bindSplashActivity(): SplashActivity

    @BeaconSettingsActivityScope
    @ContributesAndroidInjector(modules = [BeaconSettingsActivityModule::class, FragmentsModule::class])
    abstract fun bindBeaconSettingsActivity(): BeaconSettingsActivity

    @DfuActivityScope
    @ContributesAndroidInjector
    abstract fun bindDfuActivity(): DfuActivity

    @AlarmServiceScope
    @ContributesAndroidInjector(modules = [AlarmServiceModule::class])
    abstract fun bindAlarmService(): AlarmService
}
