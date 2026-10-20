package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ThemeType
import com.example.ui.theme.*

@Composable
fun GameOverDialog(
    score: Int,
    highScore: Int,
    linesCleared: Int,
    highestCombo: Int,
    isTimeOut: Boolean = false,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    val strings = com.example.localization.LocalAppStrings.current
    val isNewRecord = score >= highScore && score > 0

    Dialog(
        onDismissRequest = {},
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1838)),
            border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFFF3D71).copy(alpha = 0.8f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("game_over_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Game Over Header
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFF1744).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFFFF1744).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isTimeOut) strings.timeRemaining + " 0:00" else strings.gameOverTitle,
                        fontFamily = RighteousFontFamily,
                        fontSize = 24.sp,
                        color = Color(0xFFFF3D71),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cause of Game Over
                Text(
                    text = if (isTimeOut) strings.timeUp else strings.noMoreMoves,
                    fontFamily = FredokaFontFamily,
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                if (isNewRecord) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = strings.newRecordBadge,
                        fontFamily = RighteousFontFamily,
                        fontSize = 14.sp,
                        color = AccentGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Final Score Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF15102A), Color(0xFF1A1435))
                            )
                        )
                        .border(1.dp, Color(0xFF382E67), RoundedCornerShape(18.dp))
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = strings.finalScore,
                            fontFamily = FredokaFontFamily,
                            fontSize = 12.sp,
                            color = AccentGold,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$score",
                            fontFamily = RighteousFontFamily,
                            fontSize = 44.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.testTag("final_score")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = strings.linesCleared, fontFamily = FredokaFontFamily, fontSize = 11.sp, color = TextMuted)
                        Text(text = "$linesCleared", fontFamily = RighteousFontFamily, fontSize = 18.sp, color = BlockCyan)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = strings.bestCombo, fontFamily = FredokaFontFamily, fontSize = 11.sp, color = TextMuted)
                        Text(text = "x$highestCombo", fontFamily = RighteousFontFamily, fontSize = 18.sp, color = Color(0xFFFF6D00))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = strings.highScore, fontFamily = FredokaFontFamily, fontSize = 11.sp, color = TextMuted)
                        Text(text = "$highScore", fontFamily = RighteousFontFamily, fontSize = 18.sp, color = AccentGold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Restart Button
                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("restart_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart", tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.restart,
                        fontFamily = RighteousFontFamily,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Main Menu Button
                OutlinedButton(
                    onClick = onMainMenu,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF4C3A7A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("main_menu_button")
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Main Menu", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = strings.mainMenu, fontFamily = FredokaFontFamily, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun LevelCompleteDialog(
    levelNumber: Int,
    score: Int,
    rewardCoins: Int,
    onNextLevel: () -> Unit,
    onMainMenu: () -> Unit
) {
    val strings = com.example.localization.LocalAppStrings.current

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1838)),
            border = androidx.compose.foundation.BorderStroke(2.dp, AccentGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("level_complete_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${strings.levelPrefix} $levelNumber ${strings.levelWonTitle}",
                    fontFamily = RighteousFontFamily,
                    fontSize = 22.sp,
                    color = AccentGold,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3 Stars
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(42.dp))
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(54.dp))
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(42.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${strings.finalScore}: $score",
                    fontFamily = FredokaFontFamily,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Reward Coins
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF281E4B))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = "🪙", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+$rewardCoins ${strings.rewardCoins}",
                        fontFamily = RighteousFontFamily,
                        fontSize = 16.sp,
                        color = AccentGold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.buttonColors(containerColor = BlockGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("next_level_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.nextLevel,
                        fontFamily = RighteousFontFamily,
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onMainMenu,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("main_menu_button")
                ) {
                    Text(text = strings.mainMenu, fontFamily = FredokaFontFamily, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMainMenu: () -> Unit,
    isSoundEnabled: Boolean,
    isVibrationEnabled: Boolean,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    currentLanguage: com.example.localization.AppLanguage = com.example.localization.LocalCurrentLanguage.current,
    onSelectLanguage: (com.example.localization.AppLanguage) -> Unit = {}
) {
    val strings = com.example.localization.LocalAppStrings.current
    var showLanguagePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onResume) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1838)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4C3A7A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("pause_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.gamePaused,
                    fontFamily = RighteousFontFamily,
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Sound & Vibration Toggles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF15102A))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = strings.sound, fontFamily = FredokaFontFamily, color = Color.White)
                    Switch(
                        checked = isSoundEnabled,
                        onCheckedChange = { onToggleSound() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF15102A))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = strings.vibration, fontFamily = FredokaFontFamily, color = Color.White)
                    Switch(
                        checked = isVibrationEnabled,
                        onCheckedChange = { onToggleVibration() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Language Selector Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF15102A))
                        .clickable { showLanguagePicker = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("pause_language_row"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = AccentGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = strings.language, fontFamily = FredokaFontFamily, color = Color.White)
                    }
                    Text(
                        text = "${currentLanguage.flagEmoji} ${currentLanguage.displayName}",
                        fontFamily = RighteousFontFamily,
                        fontSize = 13.sp,
                        color = AccentGold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("resume_button")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = strings.resume, fontFamily = RighteousFontFamily, fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onRestart,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("restart_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = strings.restart, fontFamily = FredokaFontFamily, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onMainMenu,
                    modifier = Modifier.fillMaxWidth().testTag("main_menu_button")
                ) {
                    Text(text = strings.mainMenu, fontFamily = FredokaFontFamily, color = TextMuted, fontSize = 15.sp)
                }
            }
        }
    }

    if (showLanguagePicker) {
        LanguageSelectorDialog(
            currentLanguage = currentLanguage,
            onSelectLanguage = { newLang ->
                onSelectLanguage(newLang)
                showLanguagePicker = false
            },
            onDismiss = { showLanguagePicker = false }
        )
    }
}

@Composable
fun ThemeSelectorDialog(
    currentTheme: ThemeType,
    onSelectTheme: (ThemeType) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1838)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4C3A7A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val strings = com.example.localization.LocalAppStrings.current
                Text(
                    text = strings.chooseThemeTitle,
                    fontFamily = RighteousFontFamily,
                    fontSize = 22.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                ThemeType.values().forEach { theme ->
                    val isSelected = theme == currentTheme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFF382E67) else Color(0xFF15102A))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AccentGold else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                onSelectTheme(theme)
                                onDismiss()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = theme.displayName,
                            fontFamily = FredokaFontFamily,
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        // Preview swatch
                        Row {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(theme.bg)
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(theme.cellEmpty)
                            )
                        }
                    }
                }
            }
        }
    }
}
