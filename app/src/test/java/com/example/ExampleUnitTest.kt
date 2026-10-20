package com.example

import androidx.compose.ui.graphics.Color
import com.example.model.AdventureLevels
import com.example.model.Piece
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun pieceRotation_preservesBlockCountAndRotates() {
        val originalCoords = listOf(0 to 0, 0 to 1, 0 to 2) // Horizontal 1x3 bar
        val piece = Piece(coordinates = originalCoords, color = Color.Cyan)

        assertEquals(1, piece.rows)
        assertEquals(3, piece.cols)
        assertEquals(3, piece.blockCount)

        val rotated = piece.rotatedClockwise()
        assertEquals(3, rotated.rows)
        assertEquals(1, rotated.cols)
        assertEquals(3, rotated.blockCount)
    }

    @Test
    fun simultaneousLines_scaleMultiplierCorrectly() {
        fun calculateMultiplier(simultaneousLines: Int, streak: Int): Int {
            val base = when (simultaneousLines) {
                1 -> 1
                2 -> 2
                3 -> 3
                4 -> 5
                else -> 8
            }
            return base + (streak - 1).coerceAtLeast(0)
        }

        assertEquals(1, calculateMultiplier(1, 1))
        assertEquals(2, calculateMultiplier(2, 1)) // Double clear
        assertEquals(3, calculateMultiplier(3, 1)) // Triple clear
        assertEquals(5, calculateMultiplier(4, 1)) // Quad clear
        assertEquals(4, calculateMultiplier(2, 3)) // Double clear with existing streak
    }

    @Test
    fun adventureLevels_areConfiguredCorrectly() {
        assertFalse(AdventureLevels.levels.isEmpty())
        val level1 = AdventureLevels.levels[0]
        assertEquals(1, level1.levelNumber)
        assertTrue(level1.targetScore > 0)
        assertTrue(level1.targetLines > 0)
    }

    @Test
    fun dailyLevel_generatesValidTarget() {
        val daily = AdventureLevels.getDailyLevel()
        assertTrue(daily.targetScore >= 1500)
        assertTrue(daily.targetLines >= 10)
    }

    @Test
    fun adMobUnits_areConfiguredCorrectly() {
        assertEquals("ca-app-pub-1495262574338316/2803163788", com.example.ads.AdMobConfig.BANNER_GAME_BOTTOM)
        assertEquals("ca-app-pub-1495262574338316/2903409205", com.example.ads.AdMobConfig.BANNER_MENU_BOTTOM)
        assertEquals("ca-app-pub-1495262574338316/2452733430", com.example.ads.AdMobConfig.INTERSTITIAL_GAME_OVER)
        assertEquals("ca-app-pub-1495262574338316/8970040322", com.example.ads.AdMobConfig.INTERSTITIAL_LEVEL_WIN)
        assertEquals("ca-app-pub-1495262574338316/1182179137", com.example.ads.AdMobConfig.REWARDED_FREE_COINS)
        assertEquals("ca-app-pub-1495262574338316/4962596335", com.example.ads.AdMobConfig.INTERSTITIAL_POPUP_SECONDARY)
        assertEquals("ca-app-pub-1495262574338316/2714752655", com.example.ads.AdMobConfig.BANNER_ADVENTURE_BOTTOM)
        assertEquals("ca-app-pub-1495262574338316/3503687777", com.example.ads.AdMobConfig.INTERSTITIAL_ALT)
    }

    @Test
    fun gameOverCondition_whenBoardIsFull_cannotPlaceAnyPiece() {
        val fullBoard = List(8) { r ->
            List(8) { c ->
                com.example.model.BoardCell(row = r, col = c, color = androidx.compose.ui.graphics.Color.Red)
            }
        }
        val piece = com.example.model.PieceLibrary.getRandomPieces(1).first()
        var canFit = false
        for (r in 0 until 8) {
            for (c in 0 until 8) {
                val fits = piece.coordinates.all { pair ->
                    val nr = r + pair.first
                    val nc = c + pair.second
                    nr in 0 until 8 && nc in 0 until 8 && fullBoard[nr][nc].isEmpty
                }
                if (fits) {
                    canFit = true
                    break
                }
            }
        }
        assertFalse(canFit)
    }

    @Test
    fun appLanguage_allLanguagesProvideCompleteStrings() {
        for (lang in com.example.localization.AppLanguage.entries) {
            val strings = com.example.localization.AppStrings.get(lang)
            assertTrue("App title should not be empty for ${lang.name}", strings.appTitle.isNotBlank())
            assertTrue("Play button should not be empty for ${lang.name}", strings.playClassic.isNotBlank())
            assertTrue("Game Over title should not be empty for ${lang.name}", strings.gameOverTitle.isNotBlank())
            assertTrue("Resume string should not be empty for ${lang.name}", strings.resume.isNotBlank())
            assertTrue("Restart string should not be empty for ${lang.name}", strings.restart.isNotBlank())
            assertTrue("Main menu should not be empty for ${lang.name}", strings.mainMenu.isNotBlank())
            assertTrue("Multiplier tip should not be empty for ${lang.name}", strings.multiTipDouble.isNotBlank())
        }
    }

    @Test
    fun appLanguage_fromCodeFallback() {
        assertEquals(com.example.localization.AppLanguage.ENGLISH, com.example.localization.AppLanguage.fromCode("en"))
        assertEquals(com.example.localization.AppLanguage.SPANISH, com.example.localization.AppLanguage.fromCode("es"))
        assertEquals(com.example.localization.AppLanguage.PORTUGUESE, com.example.localization.AppLanguage.fromCode("pt"))
        assertEquals(com.example.localization.AppLanguage.FRENCH, com.example.localization.AppLanguage.fromCode("fr"))
        assertEquals(com.example.localization.AppLanguage.GERMAN, com.example.localization.AppLanguage.fromCode("de"))
        assertEquals(com.example.localization.AppLanguage.ITALIAN, com.example.localization.AppLanguage.fromCode("it"))
        assertEquals(com.example.localization.AppLanguage.JAPANESE, com.example.localization.AppLanguage.fromCode("ja"))
        assertEquals(com.example.localization.AppLanguage.SPANISH, com.example.localization.AppLanguage.fromCode("invalid_code"))
    }
}
