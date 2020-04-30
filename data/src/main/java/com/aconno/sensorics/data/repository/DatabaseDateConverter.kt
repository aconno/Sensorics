package com.aconno.sensorics.data.repository

import androidx.room.TypeConverter
import java.util.*


class DatabaseDateConverter {

    @TypeConverter
    fun toDate(dateLong : Long?) : Date? {
        return dateLong?.let { Date(dateLong) } //intentionally converting null to Date(0) so that devices without timeAdded attribute get treated as oldest
    }

    @TypeConverter
    fun fromDate(date : Date?) : Long? {
        return date?.time
    }
}