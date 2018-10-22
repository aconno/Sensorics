package com.aconno.sensorics.device.format

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.AdvertisementFormatJsonConverter
import com.aconno.sensorics.domain.format.RemoteAdvertisementFormat
import com.aconno.sensorics.domain.repository.LocalFormatRepository
import com.aconno.sensorics.domain.repository.RemoteFormatRepository
import io.reactivex.Completable
import java.text.SimpleDateFormat
import java.util.*

class RemoteFormatRepositoryImpl(
    private val retrofitAdvertisementFormatApi: RetrofitAdvertisementFormatApi,
    private val localFormatRepository: LocalFormatRepository,
    private val advertisementFormatReader: AdvertisementFormatReader,
    private val advertisementJsonConverter: AdvertisementFormatJsonConverter
) : RemoteFormatRepository {

    private val cachedFormats = mutableListOf<AdvertisementFormat>()

    override fun updateAdvertisementFormats(): Completable {
        return Completable.fromAction {
            try {
                loadFormatsRemotely()
            } catch (e: Exception) {
                loadFormatsFromAssets()
            }
        }
    }

    private fun loadFormatsRemotely() {
        val formatsListingCall = retrofitAdvertisementFormatApi.getFormatsListing()
        val formatsListing = formatsListingCall.execute().body()

        if (formatsListing != null) {
            val names = listingsToNames(formatsListing)

            removeUnusedFormats(names)
            names.forEach { updateFormat(it) }

            cachedFormats.addAll(localFormatRepository.getAllFormats())

        } else {
            throw IllegalStateException("There are no formats on the server.")
        }
    }

    private fun loadFormatsFromAssets() {
        val formats = advertisementFormatReader.getPreloadedFormats()
        cachedFormats.clear()
        val output = formats.map { advertisementJsonConverter.toAdvertisementFormat(it) }
        cachedFormats.addAll(output)
    }

    override fun getSupportedAdvertisementFormats(): List<AdvertisementFormat> = cachedFormats

    private fun removeUnusedFormats(formatIds: List<String>) {
        localFormatRepository.getAllFormatIds().filter { it !in formatIds }
            .forEach { localFormatRepository.deleteFormat(it) }
    }

    private fun updateFormat(formatName: String) {
        if (needsToUpdate(formatName)) {
            val format = getRemoteAdvertisementFormat(formatName)
            storeFormat(format)
        }
    }

    private fun storeFormat(format: RemoteAdvertisementFormat) {
        localFormatRepository.addOrReplaceFormat(format.id, format.serverTimestamp, format.format)
    }

    private fun needsToUpdate(formatName: String): Boolean {
        val remoteModified =
            retrofitAdvertisementFormatApi.getLastModifiedDate(formatName).execute().headers()
                .get("Last-Modified")
        if (remoteModified != null) {
            val localModified = localFormatRepository.getLastUpdateTimestamp(formatName)

            return convertDateStringToTimestamp(remoteModified) > localModified
        } else {
            throw IllegalStateException("Network error")
        }

    }

    private fun listingsToNames(formatsListing: String) =
        formatsListing
            .split("\n")
            .asSequence()
            .map { listingToIdName(it) }
            .filter { it.matches(Regex("[0-9a-fA-F]{4}")) }
            .toList()


    private fun listingToIdName(listing: String) = listing.split(Regex("\\s+")).last()

    private fun convertDateStringToTimestamp(date: String): Long {
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.parse(date).time
    }

    private fun getRemoteAdvertisementFormat(sensorId: String): RemoteAdvertisementFormat {

        val modified =
            retrofitAdvertisementFormatApi.getLastModifiedDate(sensorId).execute().headers()
                .get("Last-Modified")
        val format = retrofitAdvertisementFormatApi.getFormat(sensorId).execute().body()

        if (modified != null && format != null) {

            val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val timestamp = dateFormat.parse(modified).time

            return RemoteAdvertisementFormat(sensorId, timestamp, format)
        } else {
            throw IllegalStateException("Network issue")
        }

    }
}