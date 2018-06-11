package com.aconno.acnsensa.model

data class GenericFormatModel(
    val formatName: String,
    val icon: String,
    val format: List<ByteFormatModel>,
    val requiredFormat: List<ByteFormatRequiredModel>
)