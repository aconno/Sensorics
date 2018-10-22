package com.aconno.sensorics.dagger.application

import android.support.v4.content.LocalBroadcastManager
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.BluetoothStateReceiver
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.dagger.action_details.ActionDetailsActivityScope
import com.aconno.sensorics.dagger.action_details.ActionDetailsModule
import com.aconno.sensorics.dagger.actionlist.ActionListModule
import com.aconno.sensorics.dagger.actionlist.ActionListScope
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceModule
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceScope
import com.aconno.sensorics.dagger.deviceselect.DeviceSelectModule
import com.aconno.sensorics.dagger.deviceselect.DeviceSelectScope
import com.aconno.sensorics.dagger.gcloudpublisher.GoogleCloudPublisherModule
import com.aconno.sensorics.dagger.gcloudpublisher.GoogleCloudPublisherScope
import com.aconno.sensorics.dagger.mainactivity.MainActivityModule
import com.aconno.sensorics.dagger.mainactivity.MainActivityScope
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherModule
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherScope
import com.aconno.sensorics.dagger.publish.PublishListModule
import com.aconno.sensorics.dagger.publish.PublishListScope
import com.aconno.sensorics.dagger.restpublisher.RESTPublisherModule
import com.aconno.sensorics.dagger.restpublisher.RESTPublisherScope
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.domain.SmsSender
import com.aconno.sensorics.domain.Vibrator
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinder
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.interactor.convert.ReadingToInputUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanDevice
import com.aconno.sensorics.domain.repository.DeviceRepository
import com.aconno.sensorics.domain.repository.InMemoryRepository
import com.aconno.sensorics.domain.repository.Settings
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.ui.*
import com.aconno.sensorics.ui.acnrange.AcnRangeFragment
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import com.aconno.sensorics.ui.devicecon.AcnFreightFragment
import com.aconno.sensorics.ui.devices.SavedDevicesFragment
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialog
import com.aconno.sensorics.ui.sensors.SensorListFragment
import com.aconno.sensorics.ui.settings.publishers.DeviceSelectFragment
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity
import com.aconno.sensorics.ui.settings.publishers.PublishListFragment
import com.aconno.sensorics.ui.settings.publishers.selectpublish.GoogleCloudPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.MqttPublisherActivity
import com.aconno.sensorics.ui.settings.publishers.selectpublish.RESTPublisherActivity
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.reactivex.Flowable
import javax.inject.Scope
import javax.inject.Singleton

@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        DataModule::class,
        FormatModule::class, ActivityBuilder::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<SensoricsApplication> {

    fun bluetooth(): Bluetooth

    fun inMemoryRepository(): InMemoryRepository

    fun scannedDevice(): Flowable<ScanDevice>

    fun savedDevices(): Flowable<List<Device>>

    fun filterByMacUseCase(): FilterByMacUseCase

    fun googlePublishRepository(): GooglePublishRepository

    fun restPublishRepository(): RESTPublishRepository

    fun mqttPublishRepository(): MqttPublishRepository

    fun actionsRepository(): ActionsRepository

    fun notificationDisplay(): NotificationDisplay

    fun vibrator(): Vibrator

    fun smsSender(): SmsSender

    fun textToSpeechPlayer(): TextToSpeechPlayer

    fun bluetoothStateReceiver(): BluetoothStateReceiver

    fun localBroadcastManager(): LocalBroadcastManager

    fun intentProvider(): IntentProvider

    fun deviceRepository(): DeviceRepository

    fun readingToInputUseCase(): ReadingToInputUseCase

    fun publishDeviceJoinRepository(): PublishDeviceJoinRepository

    fun readingsStream(): Flowable<List<Reading>>

    fun getSavedDevicesMaybeUseCase(): GetSavedDevicesMaybeUseCase

    fun connectionCharacteristicsFinder(): ConnectionCharacteristicsFinder

    fun formatMatcher(): FormatMatcher

    fun settings(): Settings

    fun inject(scannedDevicesDialog: ScannedDevicesDialog)

    fun inject(bluetoothConnectService: BluetoothConnectService)

    fun inject(acnFrightFragment: AcnFreightFragment)

    fun inject(splashActivity: SplashActivity)

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<SensoricsApplication>()
}

//TODO: Separate this code into multiple files.

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @MainActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class, MainActivityFragmentsModule::class])
    abstract fun bindMainActivity(): MainActivity

    @BluetoothScanningServiceScope
    @ContributesAndroidInjector(modules = [BluetoothScanningServiceModule::class])
    abstract fun bindBluetoothScanningService(): BluetoothScanningService

    @PublishListScope
    @ContributesAndroidInjector(modules = [PublishListModule::class, PublishListFragmentsModule::class])
    abstract fun bindPublishListActivity(): PublishListActivity

    @ActionListScope
    @ContributesAndroidInjector(modules = [ActionListModule::class, ActionListFragmentsModule::class])
    abstract fun bindActionListActivity(): ActionListActivity

    @ActionDetailsActivityScope
    @ContributesAndroidInjector(modules = [ActionDetailsModule::class])
    abstract fun bindActionDetailsActivity(): ActionDetailsActivity

    @GoogleCloudPublisherScope
    @ContributesAndroidInjector(modules = [GoogleCloudPublisherModule::class, DeviceSelectionModule::class])
    abstract fun bindGoogleCloudPublisherActivity(): GoogleCloudPublisherActivity

    @MqttPublisherScope
    @ContributesAndroidInjector(modules = [MqttPublisherModule::class, DeviceSelectionModule::class])
    abstract fun bindMqttPublisherActivity(): MqttPublisherActivity

    @RESTPublisherScope
    @ContributesAndroidInjector(modules = [RESTPublisherModule::class, DeviceSelectionModule::class])
    abstract fun bindRestPublisherActivity(): RESTPublisherActivity
}

@Module
abstract class ActionListFragmentsModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindActionListFragment(): ActionListFragment
}

@Module
abstract class MainActivityFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindUseCasesFragment(): UseCasesFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindSavedDevicesFragment(): SavedDevicesFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindSensorListFragment(): SensorListFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindScannedDevicesDialog(): ScannedDevicesDialog

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun acnRangeFragment(): AcnRangeFragment

}

@Module
abstract class DeviceSelectionModule {
    @DeviceSelectScope
    @ContributesAndroidInjector(modules = [DeviceSelectModule::class])
    abstract fun bindDeviceSelectFragment(): DeviceSelectFragment
}

@Module
abstract class PublishListFragmentsModule {
    @ContributesAndroidInjector
    @FragmentScope
    abstract fun bindPublishListFragment(): PublishListFragment
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentScope
