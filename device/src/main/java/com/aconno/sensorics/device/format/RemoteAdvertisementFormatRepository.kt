package com.aconno.sensorics.device.format

import com.aconno.sensorics.domain.format.RemoteAdvertisementFormat
import com.aconno.sensorics.domain.repository.AdvertisementFormatRepository
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.util.*

class RemoteAdvertisementFormatRepository(
        private val retrofitAdvertisementFormatApi: RetrofitAdvertisementFormatApi
) : AdvertisementFormatRepository {


    override fun getSupportedAdvertisementFormats(): Single<List<RemoteAdvertisementFormat>> {
        return Single.fromCallable {
            val formatsListingCall = retrofitAdvertisementFormatApi.getFormatsListing()
            val formatsListing = formatsListingCall.execute().body()

            val output = formatsListing
                    ?.split("\n")
                    ?.asSequence()
                    ?.map { listingToIdName(it) }
                    ?.filter { it.matches(Regex("[0-9a-fA-F]{4}")) }
                    ?.map { getRemoteAdvertisementFormat(it) }
                    ?.toList()

            output ?: listOf()
        }

    }

    private fun listingToIdName(listing: String) = listing.split(Regex("\\s+")).last()

    private fun getRemoteAdvertisementFormat(sensorId: String): RemoteAdvertisementFormat {

        val modified = retrofitAdvertisementFormatApi.getLastModifiedDate(sensorId).execute().headers().get("Last-Modified")
        val format = retrofitAdvertisementFormatApi.getFormat(sensorId).execute().body()

        if (modified != null && format != null) {

            val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
            dateFormat.timeZone = TimeZone.getTimeZone("GMT")
            val timestamp = dateFormat.parse(modified).time

            return RemoteAdvertisementFormat(sensorId, timestamp, format)
        } else {
            throw IllegalStateException("Network issue")
        }

    }
}