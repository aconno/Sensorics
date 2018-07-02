package com.aconno.acnsensa.data.converter

import com.aconno.acnsensa.domain.model.Reading
import java.util.*
import java.util.regex.Pattern

class DataStringConverter(userDataString: String) {

    companion object {
        private const val VALUE = "value"
    }

    private var map: HashMap<String, String>? = null

    private val pattern = Pattern.compile("\\$\\s*(\\w+)")

    init {
        parseDataString(userDataString)
    }

    private fun parseDataString(everything: String) {
        map = HashMap()

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

    fun convert(data: Reading): List<String>? {
        val type = data.type.toDataString().toLowerCase()
        val map = this.map!!

        val dataString: String

        dataString = if (map.containsKey(type)) {
            map[type]!!.replace(
                "$" + data.type.toDataString().toLowerCase(),
                data.value.toString()
            )
        } else {
            if (map.containsKey(DataStringConverter.VALUE)) {
                map[DataStringConverter.VALUE]!!.replace(
                    "\$value",
                    data.value.toString()
                )
            } else {
                return null
            }
        }

        return listOf(
            dataString
                .replace("\$ts", System.currentTimeMillis().toString())
                .replace("\$name", data.type.toDataString())
        )
    }
}
