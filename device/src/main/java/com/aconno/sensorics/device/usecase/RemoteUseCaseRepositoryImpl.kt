package com.aconno.sensorics.device.usecase

import com.aconno.sensorics.device.usecase.model.RemoteUseCase
import com.aconno.sensorics.domain.repository.LocalUseCaseRepository
import com.aconno.sensorics.domain.repository.RemoteUseCaseRepository
import io.reactivex.Completable
import java.text.SimpleDateFormat
import java.util.*

class RemoteUseCaseRepositoryImpl(
    private val retrofitUseCaseApi: RetrofitUseCaseApi,
    private val localUseCaseRepository: LocalUseCaseRepository
) : RemoteUseCaseRepository {

    override fun updateUseCases(): Completable {
        return Completable.fromAction {
            val htmlsListingCall = retrofitUseCaseApi.getHtmlListing()
            val htmlsListing = htmlsListingCall.execute().body()

            if (htmlsListing != null) {
                val names = listingsToNames(htmlsListing)

                removeUnusedFormats(names)
                names.forEach { updateFormat(it) }
            } else {
                throw IllegalStateException("There are no UseCases on the server.")
            }

        }
    }

    private fun removeUnusedFormats(formatIds: List<String>) {
        localUseCaseRepository.getAllUseCaseNames().filter { it !in formatIds }
            .forEach { localUseCaseRepository.deleteUseCase(it) }
    }

    private fun updateFormat(formatName: String) {
        if (needsToUpdate(formatName)) {
            val format = getRemoteUseCase(formatName)
            storeFormat(format)
        }
    }

    private fun storeFormat(format: RemoteUseCase) {
        localUseCaseRepository.saveOrReplaceUseCase(
            format.name,
            format.serverTimestamp,
            format.html
        )
    }

    private fun needsToUpdate(formatName: String): Boolean {
        val remoteModified =
            retrofitUseCaseApi.getLastModifiedDate(formatName).execute().headers()
                .get("Last-Modified")
        if (remoteModified != null) {
            val localModified = localUseCaseRepository.getLastUpdateTimestamp(formatName)!!

            return convertDateStringToTimestamp(remoteModified) > localModified
        } else {
            throw IllegalStateException("Network error")
        }

    }

    private fun convertDateStringToTimestamp(date: String): Long {
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return dateFormat.parse(date).time
    }

    private fun getRemoteUseCase(sensorName: String): RemoteUseCase {

        val modified =
            retrofitUseCaseApi.getLastModifiedDate(sensorName).execute().headers()
                .get("Last-Modified")
        val format = retrofitUseCaseApi.getHtml(sensorName).execute().body()

        if (modified != null && format != null) {

            val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val timestamp = dateFormat.parse(modified).time

            return RemoteUseCase(sensorName, timestamp, format)
        } else {
            throw IllegalStateException("Network issue")
        }

    }

    private fun listingsToNames(formatsListing: String) =
        formatsListing
            .split("\n")
            .asSequence()
            .filter { it.endsWith(".html") }
            .toList()
}