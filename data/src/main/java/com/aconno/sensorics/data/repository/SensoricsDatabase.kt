package com.aconno.sensorics.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aconno.sensorics.data.repository.action.ActionDao
import com.aconno.sensorics.data.repository.action.ActionEntity
import com.aconno.sensorics.data.repository.azuremqttpublish.AzureMqttPublishDao
import com.aconno.sensorics.data.repository.azuremqttpublish.AzureMqttPublishEntity
import com.aconno.sensorics.data.repository.devices.DeviceDao
import com.aconno.sensorics.data.repository.devices.DeviceEntity
import com.aconno.sensorics.data.repository.googlepublish.GooglePublishDao
import com.aconno.sensorics.data.repository.googlepublish.GooglePublishEntity
import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishDao
import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishEntity
import com.aconno.sensorics.data.repository.mqttvirtualscanningsource.MqttVirtualScanningSourceDao
import com.aconno.sensorics.data.repository.mqttvirtualscanningsource.MqttVirtualScanningSourceEntity
import com.aconno.sensorics.data.repository.publishdevicejoin.*
import com.aconno.sensorics.data.repository.restpublish.RESTPublishDao
import com.aconno.sensorics.data.repository.restpublish.RestHeaderEntity
import com.aconno.sensorics.data.repository.restpublish.RestHttpGetParamEntity
import com.aconno.sensorics.data.repository.restpublish.RestPublishEntity
import com.aconno.sensorics.data.repository.sync.SyncDao
import com.aconno.sensorics.data.repository.sync.SyncEntity

@Database(
    entities = [
        ActionEntity::class,
        DeviceEntity::class,
        GooglePublishDeviceJoinEntity::class,
        GooglePublishEntity::class,
        AzureMqttPublishEntity::class,
        MqttPublishDeviceJoinEntity::class,
        MqttPublishEntity::class,
        AzureMqttPublishDeviceJoinEntity::class,
        RestHeaderEntity::class,
        RestHttpGetParamEntity::class,
        RestPublishDeviceJoinEntity::class,
        RestPublishEntity::class,
        SyncEntity::class,
        MqttVirtualScanningSourceEntity::class
    ],
    version = 15
)
abstract class SensoricsDatabase : RoomDatabase() {

    abstract fun actionDao(): ActionDao

    abstract fun deviceDao(): DeviceDao

    abstract fun googlePublishDao(): GooglePublishDao

    abstract fun mqttPublishDao(): MqttPublishDao

    abstract fun azureMqttPublishDao(): AzureMqttPublishDao

    abstract fun mqttVirtualScanningSourceDao(): MqttVirtualScanningSourceDao

    abstract fun publishDeviceJoinDao(): PublishDeviceJoinDao

    abstract fun restPublishDao(): RESTPublishDao

    abstract fun syncDao(): SyncDao

    companion object {
        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE actions ADD COLUMN active INTEGER NOT NULL DEFAULT 1")
            }
        }
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE actions ADD COLUMN timeFrom INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE actions ADD COLUMN timeTo INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `azure_mqtt_publish` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `iotHubName` TEXT NOT NULL, `deviceId` TEXT NOT NULL, `sharedAccessKey` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `timeType` TEXT NOT NULL, `timeMillis` INTEGER NOT NULL, `lastTimeMillis` INTEGER NOT NULL, `dataString` TEXT NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `mqtt_virtual_scanning_source` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `protocol` TEXT NOT NULL, `address` TEXT NOT NULL, `port` INTEGER NOT NULL, `path` TEXT NOT NULL, `clientId` TEXT NOT NULL, `username` TEXT NOT NULL, `password` TEXT NOT NULL, `qualityOfService` INTEGER NOT NULL)");

            }
        }

        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `azure_mqtt_publish_device_join` (`aId` INTEGER NOT NULL, `dId` TEXT NOT NULL, PRIMARY KEY(`aId`, `dId`), FOREIGN KEY(`aId`) REFERENCES `azure_mqtt_publish`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`dId`) REFERENCES `devices`(`macAddress`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            }
        }
    }
}