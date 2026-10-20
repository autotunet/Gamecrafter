package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.model.AdventureLevel
import com.example.model.AdventureLevels
import com.example.repository.GamePreferences
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdventureScreen(
    prefs: GamePreferences,
    onSelectLevel: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val strings = com.example.localization.LocalAppStrings.current
    val highestUnlocked = prefs.highestUnlockedLevel

    androidx.activity.compose.BackHandler {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.adventureMapTitle,
                        fontFamily = RighteousFontFamily,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("adventure_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.mainMenu,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF281E4B))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🪙", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${prefs.coins}",
                            fontFamily = RighteousFontFamily,
                            fontSize = 14.sp,
                            color = AccentGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NeonBg)
            )
        },
        containerColor = NeonBg,
        modifier = Modifier.testTag("adventure_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = strings.adventureMapDesc,
                fontFamily = FredokaFontFamily,
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                items(AdventureLevels.levels) { level ->
                    val isUnlocked = level.levelNumber <= highestUnlocked
                    val stars = prefs.getLevelStars(level.levelNumber)

                    LevelCard(
                        level = level,
                        isUnlocked = isUnlocked,
                        stars = stars,
                        onClick = { if (isUnlocked) onSelectLevel(level.levelNumber) }
                    )
                }
            }

            // Banner automático debajo (AdMob Unit 7: ca-app-pub-1495262574338316/2714752655)
            com.example.ads.AdMobBanner(
                adUnitId = com.example.ads.AdMobConfig.BANNER_ADVENTURE_BOTTOM,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp)
            )
        }
    }
}

@Composable
fun LevelCard(
    level: AdventureLevel,
    isUnlocked: Boolean,
    stars: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFF1E1838) else Color(0xFF141026)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isUnlocked) 2.dp else 1.dp,
            color = if (isUnlocked) ButtonPurple else Color(0xFF2B224C)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(enabled = isUnlocked, onClick = onClick)
            .testTag("level_card_${level.levelNumber}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (isUnlocked) {
                // Level Number Badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(ButtonPurple, ButtonPurpleDark))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${level.levelNumber}",
                        fontFamily = RighteousFontFamily,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                // Level Title
                Text(
                    text = level.title,
                    fontFamily = FredokaFontFamily,
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                // Challenge mode indicator (Timer or Moves)
                val strings = com.example.localization.LocalAppStrings.current
                if (level.timeLimitSeconds != null) {
                    Text(
                        text = "⏱️ ${level.timeLimitSeconds}s",
                        fontFamily = RighteousFontFamily,
                        fontSize = 10.sp,
                        color = Color(0xFF00E5FF)
                    )
                } else if (level.movesLimit != null) {
                    Text(
                        text = "${level.movesLimit} ${strings.moves.lowercase()}",
                        fontFamily = FredokaFontFamily,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                // Stars row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (s in 1..3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (s <= stars) AccentGold else Color(0xFF382E67),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                // Locked Level
                val strings = com.example.localization.LocalAppStrings.current
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = strings.locked,
                            tint = Color(0xFF5B4F8A),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${strings.levelPrefix} ${level.levelNumber}",
                            fontFamily = FredokaFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF5B4F8A)
                        )
                    }
                }
            }
        }
    }
}
