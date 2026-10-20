package com.example.localization

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val flagEmoji: String
) {
    SPANISH("es", "Español", "🇪🇸"),
    ENGLISH("en", "English", "🇺🇸"),
    PORTUGUESE("pt", "Português", "🇧🇷"),
    FRENCH("fr", "Français", "🇫🇷"),
    GERMAN("de", "Deutsch", "🇩🇪"),
    ITALIAN("it", "Italiano", "🇮🇹"),
    JAPANESE("ja", "日本語", "🇯🇵");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: SPANISH
        }
    }
}
