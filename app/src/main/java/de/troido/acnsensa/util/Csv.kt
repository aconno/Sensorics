package de.troido.acnsensa.util

import org.supercsv.cellprocessor.*
import org.supercsv.cellprocessor.ift.CellProcessor
import java.math.BigDecimal

fun generateCsvParser(klass: Class<*>): CellProcessor? = when (klass) {
    Int::class.java -> ParseInt()
    Long::class.java -> ParseLong()
    Boolean::class.java -> ParseBool()
    Double::class.java -> ParseDouble()
    BigDecimal::class.java -> ParseBigDecimal()
    Char::class.java -> ParseChar()
    Enum::class.java -> ParseEnum(klass)
    else -> null
}

inline fun <reified T : Any> generateCsvProcessor(): Array<CellProcessor> =
        ctorParamTypes<T>().mapNotNull(::generateCsvParser).toTypedArray()
