package com.aconno.acnsensa.data.converter

import com.aconno.acnsensa.domain.model.Reading

import java.util.HashMap
import java.util.Scanner
import java.util.regex.Matcher
import java.util.regex.Pattern

class DataStringConverter(userDataString: String) {

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
        val dataString = map!![data.type.toDataString().toLowerCase()] ?: return null

        return listOf(
            dataString.replace("$" + data.type.toDataString().toLowerCase(), data.value.toString())
                .replace("\$ts", System.currentTimeMillis().toString())
                .replace("\$name", data.type.toDataString())
        )
    }
}
