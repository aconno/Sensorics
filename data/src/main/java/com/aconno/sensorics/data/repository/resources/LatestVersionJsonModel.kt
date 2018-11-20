package com.aconno.sensorics.data.repository.resources

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LatestVersionJsonModel(
    @SerializedName("files_to_be_updated")
    @Expose
    val filesToBeUpdated: List<FilesToBeUpdatedJsonModel>,
    @SerializedName("is_update_needed")
    @Expose
    val isUpdateNeeded: Boolean,
    @SerializedName("latest_version")
    @Expose
    val latestVersion: Int
) {
    data class FilesToBeUpdatedJsonModel(
        @SerializedName("file_last_modified_date")
        @Expose
        val fileLastModifiedDate: Long,
        @SerializedName("file_name")
        @Expose
        val fileName: String
    )
}