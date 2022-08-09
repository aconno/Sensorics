package com.aconno.sensorics.data.repository.resources

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResourceDelta(
    @SerializedName("files_to_be_updated")
    @Expose
    val files: List<VersionedFile>,
    @SerializedName("is_update_needed")
    @Expose
    val updateNeeded: Boolean,
    @SerializedName("latest_version")
    @Expose
    val latestVersion: Int
) {
    data class VersionedFile(
        @SerializedName("file_last_modified_date")
        @Expose
        val fileLastModifiedDate: Long,
        @SerializedName("file_name")
        @Expose
        val fileName: String
    )
}