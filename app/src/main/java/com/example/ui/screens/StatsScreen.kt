package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repository.GamePreferences
import com.example.ui.theme.*

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val progress: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    prefs: GamePreferences,
    onNavigateBack: () -> Unit
) {
    val totalGames = prefs.totalGamesPlayed
    val bestScore = prefs.highScoreClassic
    val totalLines = prefs.totalLinesCleared
    val maxCombo = prefs.highestComboStreak
    val coins = prefs.coins
    val unlockedLevel = prefs.highestUnlockedLevel

    androidx.activity.compose.BackHandler {
        onNavigateBack()
    }

    val achievements = listOf(
        Achievement(
            id = "first_game",
            title = "Primer Paso",
            description = "Juega tu primera partida de bloques",
            isUnlocked = totalGames >= 1,
            progress = (totalGames / 1f).coerceIn(0f, 1f)
        ),
        Achievement(
            id = "lines_50",
            title = "Rompe-Líneas",
            description = "Despeja un total de 50 líneas ($totalLines/50)",
            isUnlocked = totalLines >= 50,
            progress = (totalLines / 50f).coerceIn(0f, 1f)
        ),
        Achievement(
            id = "lines_200",
            title = "Maestro de Líneas",
            description = "Despeja un total de 200 líneas ($totalLines/200)",
            isUnlocked = totalLines >= 200,
            progress = (totalLines / 200f).coerceIn(0f, 1f)
        ),
        Achievement(
            id = "combo_3",
            title = "Combo Fuego",
            description = "Consigue una racha de Combo x3 ($maxCombo/3)",
            isUnlocked = maxCombo >= 3,
            progress = (maxCombo / 3f).coerceIn(0f, 1f)
        ),
        Achievement(
            id = "combo_5",
            title = "Combo Legendario",
            description = "Consigue una racha de Combo x5 ($maxCombo/5)",
            isUnlocked = maxCombo >= 5,
            progress = (maxCombo / 5f).coerceIn(0f, 1f)
        ),
        Achievement(
            id = "score_2000",
            title = "Puntaje de Élite",
            description = "Alcanza 2,000 puntos en modo Clásico ($bestScore/2000)",
            isUnlocked = bestScore >= 2000,
            progress = (bestScore / 2000f).coerceIn(0f, 1f)
        ),
        Achievement(
            id = "adventure_5",
            title = "Aventurero",
            description = "Supera hasta el Nivel 5 de Aventura ($unlockedLevel/5)",
            isUnlocked = unlockedLevel >= 5,
            progress = (unlockedLevel / 5f).coerceIn(0f, 1f)
        ),
        Achievement(
            id = "rich",
            title = "Tesoro Acumulado",
            description = "Acumula 250 monedas ($coins/250)",
            isUnlocked = coins >= 250,
            progress = (coins / 250f).coerceIn(0f, 1f)
        )
    )

    val strings = com.example.localization.LocalAppStrings.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.statsTitle,
                        fontFamily = RighteousFontFamily,
                        fontSize = 19.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("stats_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.mainMenu,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NeonBg)
            )
        },
        containerColor = NeonBg,
        modifier = Modifier.testTag("stats_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Summary Cards Grid
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMetricCard(
                            label = strings.highScore,
                            value = "$bestScore",
                            color = AccentGold,
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            label = strings.totalLines,
                            value = "$totalLines",
                            color = BlockCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMetricCard(
                            label = strings.maxMultiplierRecord,
                            value = "x${prefs.highestMultiplierRecord}",
                            color = Color(0xFFFF1744),
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            label = strings.highestComboStreak,
                            value = "x$maxCombo",
                            color = Color(0xFFFF6D00),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMetricCard(
                            label = strings.totalGames,
                            value = "$totalGames",
                            color = BlockPurple,
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            label = strings.currentCoins,
                            value = "$coins 🪙",
                            color = AccentGold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMetricCard(
                            label = "${strings.playTimed} ${strings.highScore}",
                            value = "${prefs.highScoreTimed}",
                            color = Color(0xFFFF9100),
                            modifier = Modifier.weight(1f)
                        )
                        val diffStr = when (prefs.selectedDifficulty) {
                            com.example.model.Difficulty.EASY -> strings.diffEasy
                            com.example.model.Difficulty.MEDIUM -> strings.diffMedium
                            com.example.model.Difficulty.HARD -> strings.diffHard
                            com.example.model.Difficulty.EXPERT -> strings.diffExpert
                            com.example.model.Difficulty.MASTER -> strings.diffMaster
                        }
                        StatMetricCard(
                            label = strings.difficulty,
                            value = diffStr,
                            color = BlockGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LOGROS Y TROFEOS (${achievements.count { it.isUnlocked }}/${achievements.size})",
                    fontFamily = RighteousFontFamily,
                    fontSize = 16.sp,
                    color = AccentGold
                )
            }

            items(achievements) { ach ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (ach.isUnlocked) Color(0xFF1E1838) else Color(0xFF151026)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.2.dp,
                        color = if (ach.isUnlocked) AccentGold.copy(alpha = 0.6f) else Color(0xFF281E4B)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (ach.isUnlocked) Brush.linearGradient(listOf(AccentGold, Color(0xFFFF8F00)))
                                    else Brush.linearGradient(listOf(Color(0xFF2B224C), Color(0xFF1B1533)))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (ach.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (ach.isUnlocked) Color.Black else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ach.title,
                                fontFamily = RighteousFontFamily,
                                fontSize = 15.sp,
                                color = if (ach.isUnlocked) Color.White else Color.Gray
                            )
                            Text(
                                text = ach.description,
                                fontFamily = FredokaFontFamily,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            if (!ach.isUnlocked) {
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { ach.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = ButtonPurple,
                                    trackColor = Color(0xFF281E4B)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatMetricCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1838)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E67)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = label,
                fontFamily = FredokaFontFamily,
                fontSize = 12.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = RighteousFontFamily,
                fontSize = 22.sp,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
