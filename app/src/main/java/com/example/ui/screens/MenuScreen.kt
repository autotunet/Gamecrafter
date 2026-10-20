package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ThemeType
import com.example.repository.GamePreferences
import com.example.ui.components.HowToPlayDialog
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.theme.*

@Composable
fun MenuScreen(
    prefs: GamePreferences,
    onStartClassic: () -> Unit,
    onStartTimed: () -> Unit,
    onOpenAdventure: () -> Unit,
    onStartDaily: () -> Unit,
    onOpenStats: () -> Unit,
    currentLanguage: com.example.localization.AppLanguage = com.example.localization.LocalCurrentLanguage.current,
    onSelectLanguage: (com.example.localization.AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = com.example.localization.LocalAppStrings.current
    var showRulesDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var currentTheme by remember { mutableStateOf(prefs.currentTheme) }
    var currentDifficulty by remember { mutableStateOf(prefs.selectedDifficulty) }

    val diffName = when (currentDifficulty) {
        com.example.model.Difficulty.EASY -> strings.diffEasy
        com.example.model.Difficulty.MEDIUM -> strings.diffMedium
        com.example.model.Difficulty.HARD -> strings.diffHard
        com.example.model.Difficulty.EXPERT -> strings.diffExpert
        com.example.model.Difficulty.MASTER -> strings.diffMaster
    }
    val diffColor = when (currentDifficulty) {
        com.example.model.Difficulty.EASY -> BlockGreen
        com.example.model.Difficulty.MEDIUM -> BlockCyan
        com.example.model.Difficulty.HARD -> Color(0xFFFF9100)
        com.example.model.Difficulty.EXPERT -> Color(0xFFFF1744)
        com.example.model.Difficulty.MASTER -> Color(0xFFD500F9)
    }

    Scaffold(
        containerColor = currentTheme.bg,
        modifier = modifier.testTag("menu_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Wallet, Language, Difficulty & High Score Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Coins Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF281E4B))
                        .border(1.dp, AccentGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 9.dp, vertical = 6.dp)
                ) {
                    Text(text = "🪙", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${prefs.coins}",
                        fontFamily = RighteousFontFamily,
                        fontSize = 14.sp,
                        color = AccentGold
                    )
                }

                // Quick Language Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF281E4B))
                        .border(1.dp, Color(0xFF4C3A7A), RoundedCornerShape(16.dp))
                        .clickable { showLanguageDialog = true }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("menu_language_button")
                ) {
                    Text(text = currentLanguage.flagEmoji, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = currentLanguage.code.uppercase(),
                        fontFamily = RighteousFontFamily,
                        fontSize = 12.sp,
                        color = AccentGold
                    )
                }

                // Difficulty Selector Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(diffColor.copy(alpha = 0.15f))
                        .border(1.2.dp, diffColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                        .clickable { showDifficultyDialog = true }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("menu_difficulty_button")
                ) {
                    Text(text = "🎯", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = diffName.uppercase(),
                        fontFamily = RighteousFontFamily,
                        fontSize = 11.sp,
                        color = diffColor
                    )
                }

                // High score badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1838))
                        .border(1.dp, Color(0xFF4C3A7A), RoundedCornerShape(16.dp))
                        .padding(horizontal = 9.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = strings.highScore,
                        tint = AccentGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${prefs.highScoreClassic}",
                        fontFamily = RighteousFontFamily,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Game Hero Banner
            Card(
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, ButtonPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .shadow(16.dp, RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner),
                    contentDescription = "Block Blast Rush Art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "BLOCK BLAST",
                    fontFamily = RighteousFontFamily,
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "COMBO RUSH",
                    fontFamily = RighteousFontFamily,
                    fontSize = 24.sp,
                    color = AccentGold,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Play Mode Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Classic Mode (Primary Action)
                Button(
                    onClick = onStartClassic,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .shadow(12.dp, RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(listOf(ButtonPurple, Color(0xFF9333EA), Color(0xFFC026D3))),
                            RoundedCornerShape(20.dp)
                        )
                        .testTag("play_classic_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.playClassic,
                            fontFamily = RighteousFontFamily,
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Timed Rush Mode (Temporizador / Contrarreloj)
                Button(
                    onClick = onStartTimed,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFFF9100), Color(0xFFFF3D00), Color(0xFFD50000))),
                            RoundedCornerShape(20.dp)
                        )
                        .testTag("play_timed_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${strings.playTimed} (${strings.timeRushSubtitle})",
                            fontFamily = RighteousFontFamily,
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Adventure Mode
                Button(
                    onClick = onOpenAdventure,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1838)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, BlockCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("adventure_mode_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Explore, contentDescription = null, tint = BlockCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${strings.adventureMode} (${strings.adventureSubtitle})",
                            fontFamily = RighteousFontFamily,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                // Daily Challenge
                Button(
                    onClick = onStartDaily,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1838)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF6D00)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("daily_challenge_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFFFF6D00), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.dailyChallenge,
                            fontFamily = RighteousFontFamily,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                // Rewarded Video Ad Button (AdMob Unit 5: ca-app-pub-1495262574338316/1182179137)
                val context = androidx.compose.ui.platform.LocalContext.current
                val activity = context as? android.app.Activity
                Button(
                    onClick = {
                        if (activity != null) {
                            com.example.ads.AdMobManager(activity).showRewardedAd(
                                activity = activity,
                                onRewardEarned = { coinsAwarded ->
                                    prefs.coins += coinsAwarded
                                },
                                onClosed = {}
                            )
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF281E4B)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, AccentGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("rewarded_ad_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🎬", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.watchAdBonus,
                            fontFamily = RighteousFontFamily,
                            fontSize = 14.sp,
                            color = AccentGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Navigation Pills: Stats, Themes, Language, Rules
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MenuPillButton(
                    icon = Icons.Default.EmojiEvents,
                    label = strings.trophies,
                    onClick = onOpenStats
                )
                MenuPillButton(
                    icon = Icons.Default.Palette,
                    label = strings.themes,
                    onClick = { showThemeDialog = true }
                )
                MenuPillButton(
                    icon = Icons.Default.Language,
                    label = strings.language,
                    onClick = { showLanguageDialog = true }
                )
                MenuPillButton(
                    icon = Icons.Default.HelpOutline,
                    label = strings.rules,
                    onClick = { showRulesDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Banner automático debajo (AdMob Unit 2: ca-app-pub-1495262574338316/2903409205)
            com.example.ads.AdMobBanner(
                adUnitId = com.example.ads.AdMobConfig.BANNER_MENU_BOTTOM,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        if (showRulesDialog) {
            HowToPlayDialog(onDismiss = { showRulesDialog = false })
        }

        if (showThemeDialog) {
            ThemeSelectorDialog(
                currentTheme = currentTheme,
                onSelectTheme = { selected ->
                    currentTheme = selected
                    prefs.currentTheme = selected
                },
                onDismiss = { showThemeDialog = false }
            )
        }

        if (showDifficultyDialog) {
            com.example.ui.components.DifficultySelectorDialog(
                currentDifficulty = currentDifficulty,
                onSelectDifficulty = { selected ->
                    currentDifficulty = selected
                    prefs.selectedDifficulty = selected
                },
                onDismiss = { showDifficultyDialog = false }
            )
        }

        if (showLanguageDialog) {
            com.example.ui.components.LanguageSelectorDialog(
                currentLanguage = currentLanguage,
                onSelectLanguage = { selectedLang ->
                    prefs.appLanguage = selectedLang
                    onSelectLanguage(selectedLang)
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}

@Composable
private fun MenuPillButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E1838))
                .border(1.5.dp, Color(0xFF382E67), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = AccentGold, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = FredokaFontFamily,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}
