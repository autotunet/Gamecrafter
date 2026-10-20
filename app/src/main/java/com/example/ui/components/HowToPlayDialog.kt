package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun HowToPlayDialog(onDismiss: () -> Unit) {
    val strings = com.example.localization.LocalAppStrings.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1838)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4C3A7A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("how_to_play_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.howToPlayTitle,
                    fontFamily = RighteousFontFamily,
                    fontSize = 24.sp,
                    color = AccentGold,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                RuleItem(
                    step = "1",
                    icon = Icons.Default.GridView,
                    title = strings.rule1Title,
                    description = strings.rule1Desc,
                    color = BlockCyan
                )

                RuleItem(
                    step = "2",
                    icon = Icons.Default.FlashOn,
                    title = strings.rule2Title,
                    description = strings.rule2Desc,
                    color = BlockYellow
                )

                RuleItem(
                    step = "3",
                    icon = Icons.Default.LocalFireDepartment,
                    title = strings.rule3Title,
                    description = strings.rule3Desc,
                    color = Color(0xFFFF1744)
                )

                RuleItem(
                    step = "4",
                    icon = Icons.Default.AutoAwesome,
                    title = strings.rule4Title,
                    description = strings.rule4Desc,
                    color = BlockPurple
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("close_rules_button")
                ) {
                    Text(text = strings.understandBtn, fontFamily = RighteousFontFamily, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun RuleItem(
    step: String,
    icon: ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF15102A))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(1.5.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "$step. $title",
                fontFamily = RighteousFontFamily,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = description,
                fontFamily = FredokaFontFamily,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}
