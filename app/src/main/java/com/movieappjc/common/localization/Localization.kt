package com.movieappjc.common.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import java.util.Locale

internal val defaultLocalization: Localization = Localization(Locale.ENGLISH)

private val supportedLocales: MutableSet<Locale> = mutableSetOf()

internal val localizationMap = hashMapOf<Locale, Localization>()

@Stable
data class Localization(
    val locale: Locale,
    internal val strings: MutableMap<Int, String> = mutableMapOf()
)

fun registerSupportedLocales(vararg locales: Locale): Set<Locale> {
    locales.filter { it != Locale.ENGLISH }
        .forEach {
            if (supportedLocales.add(it)) {
                registerLocalizationForLocale(it)
            }
        }
    return supportedLocales + Locale.ENGLISH
}

private fun registerLocalizationForLocale(locale: Locale) {
    localizationMap[locale] = Localization(locale)
}

/**
 * Builder function for translatable string resource
 *
 * Saves given locales into corresponding [Localization] and returns extension function
 * that can be used for string resource retrieving
 *
 * @param defaultValue string value for default localization
 * @param localeToValue dictionary of locale to string resource
 * @param id integer id of a string resource
 * @return ext function that finds string in [Localization] receiver and returns it
 */
fun translatable(
    defaultValue: String,
    localeToValue: Map<Locale, String>,
    id: Int = generateUID()
): Localization.() -> String {
    defaultLocalization.strings[id] = defaultValue
    for ((locale, value) in localeToValue.entries) {
        val localization =
            localizationMap[locale] ?: throw RuntimeException("There is no locale $locale")
        localization.strings[id] = value
    }
    return fun Localization.(): String {
        return this.strings[id] ?: defaultLocalization.strings[id]
        ?: error("There is no string called $id in localization $this")
    }
}

/**
 * Builder function for non-translatable string resource
 *
 * Saves given locales into corresponding [Localization] and returns extension function
 * that can be used for string resource retrieving
 *
 * @param defaultValue string value for default localization
 * @param id integer id of a string resource
 * @return ext function that finds string in [Localization] receiver and returns it
 */
fun nonTranslatable(defaultValue: String, id: Int = generateUID()): String {
    defaultLocalization.strings[id] = defaultValue
    return defaultLocalization.strings[id]
        ?: error("There is no string called $id in localization default")
}

val LocalLanguages = compositionLocalOf { defaultLocalization }

@Composable
fun LocalizationApp(locale: () -> Locale, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalLanguages provides (localizationMap[locale()] ?: defaultLocalization),
        content = content
    )
}