package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.R
import kotlin.random.Random

class GameSoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null

    private var soundPlaceId: Int = 0
    private var soundLineClearId: Int = 0
    private var soundMultiplierUpId: Int = 0
    private var soundGameOverId: Int = 0
    private var soundLevelWinId: Int = 0

    private val loadedSoundIds = mutableSetOf<Int>()

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isVibrationEnabled: Boolean = true

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(8)
                .setAudioAttributes(audioAttributes)
                .build()

            soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
                if (status == 0) {
                    loadedSoundIds.add(sampleId)
                }
            }

            soundPlaceId = soundPool?.load(context, R.raw.snd_place, 1) ?: 0
            soundLineClearId = soundPool?.load(context, R.raw.snd_line_clear, 1) ?: 0
            soundMultiplierUpId = soundPool?.load(context, R.raw.snd_multiplier_up, 1) ?: 0
            soundGameOverId = soundPool?.load(context, R.raw.snd_game_over, 1) ?: 0
            soundLevelWinId = soundPool?.load(context, R.raw.snd_level_win, 1) ?: 0
        } catch (_: Exception) {}
    }

    private fun playSound(soundId: Int, volume: Float = 1.0f, pitchRate: Float = 1.0f, priority: Int = 1) {
        if (!isSoundEnabled || soundId == 0) return
        try {
            soundPool?.play(
                soundId,
                volume,
                volume,
                priority,
                0,
                pitchRate.coerceIn(0.5f, 2.0f)
            )
        } catch (_: Exception) {}
    }

    /**
     * Feedback when placing a block piece onto the board.
     * Uses tactile CLICK / HEAVY_CLICK on modern devices for authentic snap feel.
     */
    fun playPlace() {
        val subtlePitch = 1.0f + (Random.nextFloat() * 0.1f - 0.05f)
        playSound(soundPlaceId, volume = 0.9f, pitchRate = subtlePitch)
        vibrateBlockPlaced()
    }

    /**
     * Feedback when lines (rows or columns) are cleared.
     * Uses pitch-scaled audio proportional to the multiplier, accompanied by an explosive waveform rumble.
     */
    fun playLineClear(linesCount: Int, multiplier: Int) {
        val pitch = 1.0f + (multiplier - 1) * 0.14f
        playSound(soundLineClearId, volume = 1.0f, pitchRate = pitch, priority = 2)

        if (multiplier > 1) {
            playSound(soundMultiplierUpId, volume = 0.95f, pitchRate = 1.0f + (multiplier - 2) * 0.12f, priority = 3)
        }

        vibrateRowCleared(linesCount, multiplier)
    }

    fun playLineClear(multiplier: Int) {
        playLineClear(1, multiplier)
    }

    /**
     * Distinct audio feedback specifically when score multipliers increase.
     */
    fun playMultiplierIncrease(multiplier: Int) {
        val pitch = 1.0f + (multiplier - 1) * 0.15f
        playSound(soundMultiplierUpId, volume = 1.0f, pitchRate = pitch, priority = 3)
        vibrateMultiplierIncrease()
    }

    fun playComboCheer() {
        playSound(soundMultiplierUpId, volume = 1.0f, pitchRate = 1.25f, priority = 3)
        vibrateMultiplierIncrease()
    }

    fun playPowerUp() {
        playSound(soundMultiplierUpId, volume = 0.85f, pitchRate = 1.1f)
        vibrateBlockPlaced()
    }

    fun playGameOver() {
        playSound(soundGameOverId, volume = 0.9f, pitchRate = 1.0f)
        vibrate(150)
    }

    fun playLevelWin() {
        playSound(soundLevelWinId, volume = 1.0f, pitchRate = 1.0f)
        vibratePattern(longArrayOf(0, 40, 50, 40, 50, 80))
    }

    /**
     * Subtle audio-haptic tick when timer countdown is low (under 10s).
     */
    fun playTickWarning() {
        playSound(soundPlaceId, volume = 0.5f, pitchRate = 1.75f)
        vibrate(15)
    }

    /**
     * Uplifting ping and haptic pulse when lines clear and grant bonus seconds to the timer.
     */
    fun playTimeBonus() {
        playSound(soundMultiplierUpId, volume = 0.85f, pitchRate = 1.4f, priority = 2)
        vibrateMultiplierIncrease()
    }

    /**
     * Haptic feedback when a block is placed on the board.
     * Uses tactile CLICK / HEAVY_CLICK on modern devices for authentic snap feel.
     */
    fun vibrateBlockPlaced() {
        if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    return
                } catch (_: Exception) {}
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(22L, 220))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(22L)
            }
        } catch (_: Exception) {}
    }

    /**
     * Haptic feedback when rows or columns are cleared.
     * Generates a dynamic multi-pulse waveform rumble proportional to the number of cleared lines and multiplier.
     */
    fun vibrateRowCleared(linesCount: Int = 1, multiplier: Int = 1) {
        if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val hasAmplitude = try { vibrator.hasAmplitudeControl() } catch (_: Exception) { false }

                when {
                    linesCount >= 3 || multiplier >= 3 -> {
                        // Massive combo / quad-triple clear: 4 crescendo pulses
                        val timings = longArrayOf(0, 25, 15, 35, 15, 55, 15, 95)
                        if (hasAmplitude) {
                            val amps = intArrayOf(0, 150, 0, 190, 0, 225, 0, 255)
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, amps, -1))
                        } else {
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                        }
                    }
                    linesCount == 2 || multiplier == 2 -> {
                        // Double clear: 3 energetic pulses
                        val timings = longArrayOf(0, 30, 20, 45, 20, 75)
                        if (hasAmplitude) {
                            val amps = intArrayOf(0, 170, 0, 215, 0, 255)
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, amps, -1))
                        } else {
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                        }
                    }
                    else -> {
                        // Single row/column clear: punchy double pulse
                        val timings = longArrayOf(0, 35, 20, 60)
                        if (hasAmplitude) {
                            val amps = intArrayOf(0, 180, 0, 240)
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, amps, -1))
                        } else {
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val duration = (45 + linesCount * 25).toLong()
                vibrator.vibrate(duration)
            }
        } catch (_: Exception) {}
    }

    /**
     * Haptic feedback when multiplier escalates.
     */
    fun vibrateMultiplierIncrease() {
        if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                    return
                } catch (_: Exception) {}
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 30, 25, 60)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(70L)
            }
        } catch (_: Exception) {}
    }

    private fun vibrate(durationMs: Long) {
        if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun vibratePattern(timings: LongArray) {
        if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            soundPool?.release()
        } catch (_: Exception) {}
        soundPool = null
        loadedSoundIds.clear()
    }
}
