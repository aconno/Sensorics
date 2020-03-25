package com.aconno.sensorics.domain.serialization

class JavascriptCallGenerator {
    fun generateCall(methodName: String, vararg args: Any): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("javascript:try{")
        stringBuilder.append(methodName)
        stringBuilder.append("(")
        args.forEachIndexed { i, arg ->
            when (arg) {
                is String -> stringBuilder.append("'").append(arg).append("'")
                is Number -> stringBuilder.append(arg)
            }
            if (args.lastIndex != i) {
                stringBuilder.append(",")
            }
        }
        stringBuilder.append(")}catch(error){console.error(error.message);}")
        return stringBuilder.toString()
    }
}
