package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ThemeType

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("block_blast_rush_prefs", Context.MODE_PRIVATE)

    var highScoreClassic: Int
        get() = prefs.getInt("high_score_classic", 0)
        set(value) = prefs.edit().putInt("high_score_classic", value).apply()

    var highScoreTimed: Int
        get() = prefs.getInt("high_score_timed", 0)
        set(value) = prefs.edit().putInt("high_score_timed", value).apply()

    var selectedDifficulty: com.example.model.Difficulty
        get() {
            val name = prefs.getString("selected_difficulty", com.example.model.Difficulty.MEDIUM.name) ?: com.example.model.Difficulty.MEDIUM.name
            return try {
                com.example.model.Difficulty.valueOf(name)
            } catch (_: Exception) {
                com.example.model.Difficulty.MEDIUM
            }
        }
        set(value) = prefs.edit().putString("selected_difficulty", value.name).apply()

    var coins: Int
        get() = prefs.getInt("coins", 120) // Initial welcome gift
        set(value) = prefs.edit().putInt("coins", value).apply()

    var highestUnlockedLevel: Int
        get() = prefs.getInt("highest_unlocked_level", 1)
        set(value) = prefs.edit().putInt("highest_unlocked_level", value).apply()

    var totalGamesPlayed: Int
        get() = prefs.getInt("total_games_played", 0)
        set(value) = prefs.edit().putInt("total_games_played", value).apply()

    var totalLinesCleared: Int
        get() = prefs.getInt("total_lines_cleared", 0)
        set(value) = prefs.edit().putInt("total_lines_cleared", value).apply()

    var highestComboStreak: Int
        get() = prefs.getInt("highest_combo_streak", 0)
        set(value) = prefs.edit().putInt("highest_combo_streak", value).apply()

    var highestMultiplierRecord: Int
        get() = prefs.getInt("highest_multiplier_record", 1)
        set(value) = prefs.edit().putInt("highest_multiplier_record", value).apply()

    var currentTheme: ThemeType
        get() {
            val name = prefs.getString("current_theme", ThemeType.NEON.name) ?: ThemeType.NEON.name
            return try {
                ThemeType.valueOf(name)
            } catch (_: Exception) {
                ThemeType.NEON
            }
        }
        set(value) = prefs.edit().putString("current_theme", value.name).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit().putBoolean("vibration_enabled", value).apply()

    var appLanguage: com.example.localization.AppLanguage
        get() {
            val code = prefs.getString("app_language", com.example.localization.AppLanguage.SPANISH.code)
            return com.example.localization.AppLanguage.fromCode(code)
        }
        set(value) = prefs.edit().putString("app_language", value.code).apply()

    fun getLevelStars(level: Int): Int {
        return prefs.getInt("level_${level}_stars", 0)
    }

    fun setLevelStars(level: Int, stars: Int) {
        val current = getLevelStars(level)
        if (stars > current) {
            prefs.edit().putInt("level_${level}_stars", stars).apply()
        }
    }
}
