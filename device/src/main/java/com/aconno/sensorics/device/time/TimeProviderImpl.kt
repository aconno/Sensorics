package com.aconno.sensorics.device.time

import com.aconno.sensorics.domain.time.TimeProvider
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class TimeProviderImpl : TimeProvider {
    override fun getLocalTimeOfDayInSeconds(): Int {
        return ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).let {
            it.hour * 3600 + it.minute * 60 + it.second
        }
    }
}