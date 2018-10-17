package com.aconno.sensorics.domain.serialization

class JavascriptCallGenerator {
    fun generateCall(methodName: String, jsonParams: String): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("javascript:try{")
        stringBuilder.append(methodName)
        stringBuilder.append("(")
        stringBuilder.append("'")
        stringBuilder.append(jsonParams)
        stringBuilder.append("'")
        stringBuilder.append(")}catch(error){console.error(error.message);}")
        return stringBuilder.toString()
    }
}
