package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Difficulty
import com.example.ui.theme.*

@Composable
fun DifficultySelectorDialog(
    currentDifficulty: Difficulty,
    onSelectDifficulty: (Difficulty) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = com.example.localization.LocalAppStrings.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1838)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4C3A7A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("difficulty_selector_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.difficulty.uppercase(),
                    fontFamily = RighteousFontFamily,
                    fontSize = 22.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                Difficulty.values().forEach { diff ->
                    val isSelected = diff == currentDifficulty

                    val name = when (diff) {
                        Difficulty.EASY -> strings.diffEasy
                        Difficulty.MEDIUM -> strings.diffMedium
                        Difficulty.HARD -> strings.diffHard
                        Difficulty.EXPERT -> strings.diffExpert
                        Difficulty.MASTER -> strings.diffMaster
                    }

                    val color = when (diff) {
                        Difficulty.EASY -> BlockGreen
                        Difficulty.MEDIUM -> BlockCyan
                        Difficulty.HARD -> Color(0xFFFF9100)
                        Difficulty.EXPERT -> Color(0xFFFF1744)
                        Difficulty.MASTER -> Color(0xFFD500F9)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFF2E2452) else Color(0xFF15102A))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) color else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                onSelectDifficulty(diff)
                                onDismiss()
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("difficulty_item_${diff.name.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = name,
                                    fontFamily = RighteousFontFamily,
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Row {
                                    repeat(diff.stars) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = color,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Nivel ${diff.level} • ${if (diff.minScore > 0) "+${diff.minScore} pts" else "Inicio"}",
                                fontFamily = FredokaFontFamily,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = color,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
