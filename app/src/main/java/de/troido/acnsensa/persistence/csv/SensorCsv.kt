package de.troido.acnsensa.persistence.csv

import android.os.Environment
import de.troido.acnsensa.util.ctorParamNames
import de.troido.acnsensa.util.generateCsvProcessor
import org.supercsv.cellprocessor.ift.CellProcessor
import org.supercsv.io.CsvBeanWriter
import org.supercsv.prefs.CsvPreference
import java.io.File
import java.io.FileWriter

private const val APP_DIR = "SensorBoard"
private const val RAW_CSV_FILENAME = "rawSensorReadings.csv"
private const val COUNT_CSV_FILENAME = "counters.csv"
private const val STAT_CSV_FILENAME = "stats.csv"

private val rawNameMapping = ctorParamNames<SensorRawCsvRecord>()

private val counterNameMapping = ctorParamNames<SensorCounterCsvRecord>()

private val statsNameMapping = ctorParamNames<SensorStatsCsvRecord>()

private val rawCsvProcessor = generateCsvProcessor<SensorRawCsvRecord>()

private val counterCsvProcessor = generateCsvProcessor<SensorCounterCsvRecord>()

private val statsCsvProcessor = generateCsvProcessor<SensorStatsCsvRecord>()

private fun csvProcessor(record: SensorCsvRecord): Array<CellProcessor> = when (record) {
    is SensorRawCsvRecord -> rawCsvProcessor
    is SensorCounterCsvRecord -> counterCsvProcessor
    is SensorStatsCsvRecord -> statsCsvProcessor
}

private fun csvFileName(record: SensorCsvRecord): String = when (record) {
    is SensorRawCsvRecord -> RAW_CSV_FILENAME
    is SensorCounterCsvRecord -> COUNT_CSV_FILENAME
    is SensorStatsCsvRecord -> STAT_CSV_FILENAME
}

private fun csvNameMapping(record: SensorCsvRecord): Array<String> = when (record) {
    is SensorRawCsvRecord -> rawNameMapping
    is SensorCounterCsvRecord -> counterNameMapping
    is SensorStatsCsvRecord -> statsNameMapping
}

/**
 * Persists the given record to the application's CSV file, appending the file if it already exists.
 * The file uses a standard CSV configuration defined by [CsvPreference.STANDARD_PREFERENCE]
 * and utilizes a header with the individual column names for readability.
 */
fun persistToCsv(record: SensorCsvRecord) {
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        val appDir = File(Environment.getExternalStorageDirectory(), APP_DIR).apply { mkdirs() }
        val appCsv = File(appDir, csvFileName(record))

        val nameMapping = csvNameMapping(record)

        if (!appCsv.exists()) {
            appCsv.createNewFile()
            // We need to write the CSV header upon file creation for better readability.
            CsvBeanWriter(FileWriter(appCsv), CsvPreference.STANDARD_PREFERENCE).use {
                it.writeHeader(*nameMapping)
            }
        }

        CsvBeanWriter(FileWriter(appCsv, true), CsvPreference.STANDARD_PREFERENCE).use {
            it.write(record, nameMapping, csvProcessor(record))
        }
    }
}
