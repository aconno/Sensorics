package com.aconno.sensorics.device.usecase

import com.aconno.sensorics.device.usecase.model.RemoteUseCase
import com.aconno.sensorics.domain.repository.LocalUseCaseRepository
import com.aconno.sensorics.domain.repository.RemoteUseCaseRepository
import io.reactivex.Maybe
import timber.log.Timber
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*

class RemoteUseCaseRepositoryImpl(
    private val retrofitUseCaseApi: RetrofitUseCaseApi,
    private val localUseCaseRepository: LocalUseCaseRepository
) : RemoteUseCaseRepository {

    override fun updateUseCases(sensorName: String): Maybe<String> {
        val sensorName = sensorName.toLowerCase()

        return Maybe.fromCallable {
            try {
                updateFormat(sensorName)
            } catch (ex: Exception) {
                //No-Op
            }

            val filePath = localUseCaseRepository.getFilePathFor(sensorName)
            if (filePath.isBlank()) {
                throw NullPointerException("There are no UseCase file defined.")
            }

            filePath
        }
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

    private fun needsToUpdate(sensorName: String): Boolean {
        val remoteModified =
            retrofitUseCaseApi.getLastModifiedDate(sensorName).execute().headers()
                .get("Last-Modified")
        if (remoteModified != null) {
            val localModified = localUseCaseRepository.getLastUpdateTimestamp(sensorName)

            return convertDateStringToTimestamp(remoteModified) > localModified!!
        } else {
            throw IllegalStateException("Network error")
        }

    }

    private fun convertDateStringToTimestamp(date: String): Long {
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.parse(date).time
    }

    private fun getRemoteUseCase(sensorName: String): RemoteUseCase {

        val execute = retrofitUseCaseApi.getLastModifiedDate(sensorName).execute()

        if (execute.isSuccessful) {
            val modified = execute.headers().get("Last-Modified")

            val format = retrofitUseCaseApi.getHtml(sensorName).execute().body()

            if (modified != null && format != null) {

                val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val timestamp = dateFormat.parse(modified).time

                return RemoteUseCase(sensorName, timestamp, format)
            } else {
                throw IllegalStateException("Network issue")
            }
        } else if (execute.code() == 404) {
            localUseCaseRepository.deleteUseCase(sensorName)
            Timber.d("UseCase removed from remote, removing it from local too..")
            throw IllegalStateException("File Not Found")

        } else {
            throw IllegalStateException("Network issue")
        }

    }
}