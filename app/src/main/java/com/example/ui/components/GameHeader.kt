package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Difficulty
import com.example.model.GameMode
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun GameHeader(
    score: Int,
    highScore: Int,
    currentMultiplier: Int,
    comboStreak: Int,
    coins: Int,
    movesRemaining: Int?,
    targetScore: Int?,
    gameMode: GameMode,
    isSoundEnabled: Boolean,
    onSoundToggle: () -> Unit,
    onPauseClick: () -> Unit,
    timeRemainingSeconds: Int? = null,
    initialTimeLimitSeconds: Int? = null,
    isTimeCritical: Boolean = false,
    recentTimeBonusSeconds: Int? = null,
    difficulty: Difficulty = Difficulty.EASY,
    modifier: Modifier = Modifier
) {
    val strings = com.example.localization.LocalAppStrings.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isTimeCritical) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val diffName = when (difficulty) {
        Difficulty.EASY -> strings.diffEasy
        Difficulty.MEDIUM -> strings.diffMedium
        Difficulty.HARD -> strings.diffHard
        Difficulty.EXPERT -> strings.diffExpert
        Difficulty.MASTER -> strings.diffMaster
    }
    val diffColor = when (difficulty) {
        Difficulty.EASY -> BlockGreen
        Difficulty.MEDIUM -> BlockCyan
        Difficulty.HARD -> Color(0xFFFF9100)
        Difficulty.EXPERT -> Color(0xFFFF1744)
        Difficulty.MASTER -> Color(0xFFD500F9)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // Top Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coins Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF281E4B))
                    .border(1.dp, AccentGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("coins_badge")
            ) {
                Text(text = "🪙", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$coins",
                    fontFamily = RighteousFontFamily,
                    fontSize = 14.sp,
                    color = AccentGold
                )
            }

            // Center Badge: Prominent Timer or Score Multiplier Pill
            if (timeRemainingSeconds != null) {
                val mins = timeRemainingSeconds / 60
                val secs = timeRemainingSeconds % 60
                val timeString = String.format(Locale.getDefault(), "%d:%02d", mins, secs)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .scale(pulseScale)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isTimeCritical) {
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF1744), Color(0xFFD50000))
                                )
                            } else {
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF0284C7), Color(0xFF2563EB))
                                )
                            }
                        )
                        .border(
                            width = if (isTimeCritical) 2.dp else 1.dp,
                            color = if (isTimeCritical) Color(0xFFFFD600) else Color(0xFF38BDF8),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                        .testTag("timer_badge")
                ) {
                    Icon(
                        imageVector = if (isTimeCritical) Icons.Default.HourglassBottom else Icons.Default.Timer,
                        contentDescription = strings.timeRemaining,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = timeString,
                        fontFamily = RighteousFontFamily,
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (recentTimeBonusSeconds != null) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "+${recentTimeBonusSeconds}s",
                            fontFamily = RighteousFontFamily,
                            fontSize = 12.sp,
                            color = AccentGold,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            } else {
                // Multiplier Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (currentMultiplier > 1) {
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF1744), Color(0xFFFF6D00), Color(0xFFFFD600))
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(Color(0xFF281E4B), Color(0xFF1E1838))
                                )
                            }
                        )
                        .border(
                            width = if (currentMultiplier > 1) 1.5.dp else 1.dp,
                            color = if (currentMultiplier > 1) AccentGold else Color(0xFF4C3A7A),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 11.dp, vertical = 4.dp)
                        .testTag("multiplier_badge")
                ) {
                    Icon(
                        imageVector = if (currentMultiplier > 1) Icons.Default.Bolt else Icons.Default.FlashOn,
                        contentDescription = strings.multiplier,
                        tint = if (currentMultiplier > 1) Color.White else TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "x$currentMultiplier MULTI",
                        fontFamily = RighteousFontFamily,
                        fontSize = 13.sp,
                        color = if (currentMultiplier > 1) Color.White else TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Actions: Sound & Pause
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onSoundToggle,
                    modifier = Modifier.size(36.dp).testTag("sound_toggle")
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Toggle Sound",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onPauseClick,
                    modifier = Modifier.size(36.dp).testTag("pause_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PauseCircle,
                        contentDescription = strings.gamePaused,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Big Score Dashboard
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Score & Difficulty Badge
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = strings.score,
                        fontFamily = FredokaFontFamily,
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Difficulty pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(diffColor.copy(alpha = 0.2f))
                            .border(1.dp, diffColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${"★".repeat(difficulty.stars)} $diffName".uppercase(),
                            fontFamily = RighteousFontFamily,
                            fontSize = 10.sp,
                            color = diffColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (currentMultiplier > 1 && timeRemainingSeconds != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "x$currentMultiplier",
                            fontFamily = RighteousFontFamily,
                            fontSize = 11.sp,
                            color = AccentGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "$score",
                    fontFamily = RighteousFontFamily,
                    fontSize = 38.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("current_score")
                )
            }

            // Secondary Info: Moves or High Score
            Column(horizontalAlignment = Alignment.End) {
                if (movesRemaining != null) {
                    Text(
                        text = strings.moves,
                        fontFamily = FredokaFontFamily,
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$movesRemaining",
                        fontFamily = RighteousFontFamily,
                        fontSize = 30.sp,
                        color = if (movesRemaining <= 5) Color(0xFFFF1744) else BlockCyan,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = strings.highScore,
                        fontFamily = FredokaFontFamily,
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = strings.highScore,
                            tint = AccentGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$highScore",
                            fontFamily = RighteousFontFamily,
                            fontSize = 24.sp,
                            color = AccentGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Timer Progress Gauge (when in timed mode)
        if (timeRemainingSeconds != null && initialTimeLimitSeconds != null && initialTimeLimitSeconds > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            val timeFraction = (timeRemainingSeconds.toFloat() / initialTimeLimitSeconds.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { timeFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isTimeCritical) Color(0xFFFF1744) else Color(0xFF00E5FF),
                trackColor = Color(0xFF281E4B),
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Dedicated UI element displaying dynamic current combo multiplier
        ComboMultiplierBar(
            multiplier = currentMultiplier,
            comboStreak = comboStreak
        )

        // Level Target Progress Bar (Adventure/Daily Mode)
        if (targetScore != null && targetScore > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            val progress = (score.toFloat() / targetScore).coerceIn(0f, 1f)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${strings.target}: $targetScore pts",
                        fontFamily = FredokaFontFamily,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontFamily = RighteousFontFamily,
                        fontSize = 11.sp,
                        color = BlockGreen
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = BlockGreen,
                    trackColor = Color(0xFF281E4B),
                )
            }
        }
    }
}
