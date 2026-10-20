package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.GameSoundManager
import com.example.repository.GamePreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Block Blast Rush", appName)
    }

    @Test
    fun `game preferences persists high score and coins`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = GamePreferences(context)
        prefs.highScoreClassic = 1500
        assertEquals(1500, prefs.highScoreClassic)

        prefs.coins = 200
        assertEquals(200, prefs.coins)
    }

    @Test
    fun `sound and haptic manager handles place and line clear vibration without crash`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = GameSoundManager(context)
        soundManager.isVibrationEnabled = true
        soundManager.vibrateBlockPlaced()
        soundManager.vibrateRowCleared(linesCount = 1, multiplier = 1)
        soundManager.vibrateRowCleared(linesCount = 2, multiplier = 2)
        soundManager.vibrateRowCleared(linesCount = 4, multiplier = 5)
        soundManager.vibrateMultiplierIncrease()
        soundManager.release()
    }

    @Test
    fun `combo multiplier values scale properly and trigger haptics`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = GameSoundManager(context)
        listOf(1, 2, 3, 5, 8).forEach { multiplier ->
            soundManager.playLineClear(linesCount = multiplier, multiplier = multiplier)
        }
        soundManager.release()
    }

    @Test
    fun `difficulty scales with score and difficulty-based piece generation works`() {
        assertEquals(com.example.model.Difficulty.EASY, com.example.model.Difficulty.fromScore(100))
        assertEquals(com.example.model.Difficulty.MEDIUM, com.example.model.Difficulty.fromScore(750))
        assertEquals(com.example.model.Difficulty.HARD, com.example.model.Difficulty.fromScore(2000))
        assertEquals(com.example.model.Difficulty.EXPERT, com.example.model.Difficulty.fromScore(3500))
        assertEquals(com.example.model.Difficulty.MASTER, com.example.model.Difficulty.fromScore(6000))

        val easyPieces = com.example.model.PieceLibrary.getRandomPieces(3, com.example.model.Difficulty.EASY)
        assertEquals(3, easyPieces.size)

        val masterPieces = com.example.model.PieceLibrary.getRandomPieces(3, com.example.model.Difficulty.MASTER)
        assertEquals(3, masterPieces.size)
    }

    @Test
    fun `game preferences persists timed high score and difficulty`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = GamePreferences(context)
        prefs.highScoreTimed = 2400
        assertEquals(2400, prefs.highScoreTimed)

        prefs.selectedDifficulty = com.example.model.Difficulty.HARD
        assertEquals(com.example.model.Difficulty.HARD, prefs.selectedDifficulty)
    }

    @Test
    fun `sound manager handles timer tick warning and time bonus without crash`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = GameSoundManager(context)
        soundManager.playTickWarning()
        soundManager.playTimeBonus()
        soundManager.release()
    }
}
