package com.aconno.sensorics.data.converter

import com.aconno.sensorics.domain.model.Reading
import java.util.*
import java.util.regex.Pattern

class ReadingToStringParser() {

    companion object {
        private const val VALUE = "value"
    }

    private var map: HashMap<String, String>? = null

    private val pattern = Pattern.compile("\\$\\s*(\\w+)")

    private fun parseDataString(everything: String) {
        getHashMap()

        val scanner = Scanner(everything)
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()

            val matcher = pattern.matcher(line)
            while (matcher.find()) {
                if (!matcher.group(1).equals(
                        "ts",
                        ignoreCase = true
                    ) && !matcher.group(1).equals("name", ignoreCase = true)
                ) {
                    map!![matcher.group(1)] = line
                }
            }
        }
        scanner.close()
    }

    private fun getHashMap() {
        if (map != null) {
            map!!.clear()
        } else {
            map = HashMap()
        }
    }

    fun convert(data: Reading, stringToConvert: String): String {
        parseDataString(stringToConvert)

        val type = data.name.toLowerCase()
        val map = this.map!!

        val dataString: String

        dataString = if (map.containsKey(type)) {
            map[type]!!.replace(
                "$" + data.name.toLowerCase(),
                data.value.toString()
            )
        } else {
            if (map.containsKey(ReadingToStringParser.VALUE)) {
                map[ReadingToStringParser.VALUE]!!.replace(
                    "\$value",
                    data.value.toString()
                )
            } else {
                stringToConvert
            }
        }

        return dataString
            .replace("\$ts", System.currentTimeMillis().toString())
            .replace("\$name", data.name)
    }
}
