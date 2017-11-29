package de.troido.acnsensa.view

import android.content.Context
import android.graphics.Typeface
import java.util.*

enum class Font(val file: String) {
    PT_SANS_BOLD("PTSansBold.ttf"),
    PT_SANS_REGULAR("PTSansRegular.ttf")
}

/**
 * Singleton font cache, mapping [Font]s to [Typeface]s, and caching the results.
 */
object Fonts {
    private var cache: MutableMap<Font, Typeface> = EnumMap(Font::class.java)

    /**
     * Returns a [Typeface] defined by the given [Font].
     * The typeface will be internally cached as to avoid memory leaks.
     */
    fun get(context: Context, font: Font): Typeface =
            cache.getOrPut(font) { Typeface.createFromAsset(context.assets, font.file) }
}
