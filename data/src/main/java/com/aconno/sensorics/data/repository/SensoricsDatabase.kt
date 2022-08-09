package com.aconno.sensorics.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aconno.sensorics.data.repository.action.ActionDao
import com.aconno.sensorics.data.repository.action.ActionEntity
import com.aconno.sensorics.data.repository.azuremqttpublish.AzureMqttPublishDao
import com.aconno.sensorics.data.repository.azuremqttpublish.AzureMqttPublishEntity
import com.aconno.sensorics.data.repository.devicegroupdevicejoin.DeviceGroupDeviceJoinDao
import com.aconno.sensorics.data.repository.devicegroupdevicejoin.DeviceGroupDeviceJoinEntity
import com.aconno.sensorics.data.repository.devicegroups.DeviceGroupDao
import com.aconno.sensorics.data.repository.devicegroups.DeviceGroupEntity
import com.aconno.sensorics.data.repository.devices.DeviceDao
import com.aconno.sensorics.data.repository.devices.DeviceEntity
import com.aconno.sensorics.data.repository.googlepublish.GooglePublishDao
import com.aconno.sensorics.data.repository.googlepublish.GooglePublishEntity
import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishDao
import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishEntity
import com.aconno.sensorics.data.repository.mqttvirtualscanningsource.MqttVirtualScanningSourceDao
import com.aconno.sensorics.data.repository.mqttvirtualscanningsource.MqttVirtualScanningSourceEntity
import com.aconno.sensorics.data.repository.publishdevicejoin.GenericPublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.publishdevicejoin.PublishDeviceJoinDao
import com.aconno.sensorics.data.repository.restpublish.RESTPublishDao
import com.aconno.sensorics.data.repository.restpublish.RestHeaderEntity
import com.aconno.sensorics.data.repository.restpublish.RestHttpGetParamEntity
import com.aconno.sensorics.data.repository.restpublish.RestPublishEntity
import com.aconno.sensorics.data.repository.sync.SyncDao
import com.aconno.sensorics.data.repository.sync.SyncEntity
import com.aconno.sensorics.domain.ifttt.PublishTypeStrings

@Database(
    entities = [
        ActionEntity::class,
        DeviceEntity::class,
        GenericPublishDeviceJoinEntity::class,
        GooglePublishEntity::class,
        AzureMqttPublishEntity::class,
        MqttPublishEntity::class,
        RestHeaderEntity::class,
        RestHttpGetParamEntity::class,
        RestPublishEntity::class,
        SyncEntity::class,
        MqttVirtualScanningSourceEntity::class,
        DeviceGroupEntity::class,
        DeviceGroupDeviceJoinEntity::class
    ],
    version = 18
)
@TypeConverters(DatabaseDateConverter::class)
abstract class SensoricsDatabase : RoomDatabase() {

    abstract fun actionDao(): ActionDao

    abstract fun deviceDao(): DeviceDao

    abstract fun deviceGroupDao(): DeviceGroupDao

    abstract fun deviceGroupDeviceJoinDao(): DeviceGroupDeviceJoinDao

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
        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `publish_device_join` (`publishId` INTEGER NOT NULL, `deviceId` TEXT NOT NULL, `publishType` TEXT NOT NULL, PRIMARY KEY(`publishId`, `deviceId`, `publishType`), FOREIGN KEY(`deviceId`) REFERENCES `devices`(`macAddress`) ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                database.execSQL(
                    "INSERT INTO publish_device_join (publishId, deviceId, publishType) SELECT aId, dId, '${PublishTypeStrings.AZURE}' FROM azure_mqtt_publish_device_join"
                )
                database.execSQL(
                    "INSERT INTO publish_device_join (publishId, deviceId, publishType) SELECT gId, dId, '${PublishTypeStrings.GOOGLE}' FROM google_publish_device_join"
                )
                database.execSQL(
                    "INSERT INTO publish_device_join (publishId, deviceId, publishType) SELECT mId, dId, '${PublishTypeStrings.MQTT}' FROM mqtt_publish_device_join"
                )
                database.execSQL(
                    "INSERT INTO publish_device_join (publishId, deviceId, publishType) SELECT rId, dId, '${PublishTypeStrings.REST}' FROM rest_publish_device_join"
                )

                database.execSQL("DROP TABLE azure_mqtt_publish_device_join")
                database.execSQL("DROP TABLE google_publish_device_join")
                database.execSQL("DROP TABLE mqtt_publish_device_join")
                database.execSQL("DROP TABLE rest_publish_device_join")
            }
        }

        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `device_groups` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `device_group_device_join` (`deviceGroupId` INTEGER NOT NULL, `deviceId` TEXT NOT NULL, PRIMARY KEY(`deviceId`), FOREIGN KEY(`deviceGroupId`) REFERENCES `device_groups`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`deviceId`) REFERENCES `devices`(`macAddress`) ON UPDATE NO ACTION ON DELETE CASCADE )");

            }
        }

        val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `devices` ADD timeAdded INTEGER")
            }
        }
    }
}