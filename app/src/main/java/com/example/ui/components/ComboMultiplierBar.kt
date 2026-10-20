package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Dedicated UI element that prominently displays the active combo score multiplier during gameplay.
 * Updates dynamically with bounce animations, glowing gradients and tier indicators
 * when single or multiple rows/columns are cleared.
 */
@Composable
fun ComboMultiplierBar(
    multiplier: Int,
    comboStreak: Int,
    modifier: Modifier = Modifier
) {
    val strings = com.example.localization.LocalAppStrings.current

    // Bounce scale animation triggered on multiplier change
    val bounceScale = remember { Animatable(1f) }
    var lastMultiplier by remember { mutableIntStateOf(multiplier) }

    LaunchedEffect(multiplier) {
        if (multiplier != lastMultiplier) {
            lastMultiplier = multiplier
            if (multiplier > 1) {
                bounceScale.snapTo(1.25f)
                bounceScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = 0.52f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
        }
    }

    // Subtle breathing pulse when multiplier > 1
    val infiniteTransition = rememberInfiniteTransition(label = "multiplier_glow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val isActive = multiplier > 1

    // Dynamic gradient and colors based on the multiplier tier
    val backgroundBrush = if (isActive) {
        when {
            multiplier >= 8 -> Brush.horizontalGradient(
                listOf(Color(0xFFFF0055), Color(0xFFFF5500), Color(0xFFFFD700), Color(0xFF00E5FF))
            )
            multiplier >= 5 -> Brush.horizontalGradient(
                listOf(Color(0xFFFF1744), Color(0xFFFF5252), Color(0xFFFF9100))
            )
            multiplier >= 3 -> Brush.horizontalGradient(
                listOf(Color(0xFFFF3D00), Color(0xFFFF6D00), Color(0xFFFFAB00))
            )
            else -> Brush.horizontalGradient(
                listOf(Color(0xFFE040FB), Color(0xFFFF4081), Color(0xFFFFAB00))
            )
        }
    } else {
        Brush.horizontalGradient(
            listOf(Color(0xFF1E1838), Color(0xFF241C44), Color(0xFF1A1435))
        )
    }

    val borderColor by animateColorAsState(
        targetValue = if (isActive) AccentGold else Color(0xFF382E67),
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = bounceScale.value
                scaleY = bounceScale.value
            }
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundBrush)
            .border(if (isActive) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag("combo_multiplier_hud"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Animated Icon + Big Multiplier Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isActive) Color.White.copy(alpha = 0.22f) else Color(0xFF281E4B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (multiplier >= 4) Icons.Default.Whatshot
                        else if (multiplier > 1) Icons.Default.LocalFireDepartment
                        else Icons.Default.Bolt,
                        contentDescription = "Multiplicador de Combo",
                        tint = if (isActive) Color.White else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "x$multiplier",
                            fontFamily = RighteousFontFamily,
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.testTag("multiplier_value_text")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isActive) strings.multiActive else strings.multiplier,
                            fontFamily = RighteousFontFamily,
                            fontSize = 12.sp,
                            color = if (isActive) Color.White.copy(alpha = pulseAlpha) else TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Dynamic informative subtitle based on multiplier state
                    Text(
                        text = when {
                            multiplier >= 8 -> strings.multiTipMega
                            multiplier >= 5 -> strings.multiTipQuad
                            multiplier == 3 -> strings.multiTipTriple
                            multiplier == 2 -> strings.multiTipDouble
                            comboStreak > 0 -> "${strings.highestComboStreak}: $comboStreak"
                            else -> strings.multiTipDefault
                        },
                        fontFamily = FredokaFontFamily,
                        fontSize = 10.5.sp,
                        color = if (isActive) Color.White.copy(alpha = 0.95f) else TextMuted.copy(alpha = 0.85f),
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }

            // Right: Multiplier Tier Stepper / Visual Indicators (x1, x2, x3, x5)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.testTag("multiplier_tier_badge")
            ) {
                MultiplierStepPill(label = "x1", isReached = true, isCurrent = multiplier == 1)
                MultiplierStepPill(label = "x2", isReached = multiplier >= 2, isCurrent = multiplier == 2)
                MultiplierStepPill(label = "x3", isReached = multiplier >= 3, isCurrent = multiplier == 3)
                MultiplierStepPill(label = "x5+", isReached = multiplier >= 5, isCurrent = multiplier >= 5)
            }
        }
    }
}

@Composable
private fun MultiplierStepPill(
    label: String,
    isReached: Boolean,
    isCurrent: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                when {
                    isCurrent && isReached -> Color.White
                    isReached -> Color.White.copy(alpha = 0.35f)
                    else -> Color.Black.copy(alpha = 0.25f)
                }
            )
            .padding(horizontal = 5.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = RighteousFontFamily,
            fontSize = 10.sp,
            color = when {
                isCurrent && isReached -> Color(0xFF1E1838)
                isReached -> Color.White
                else -> Color.White.copy(alpha = 0.35f)
            },
            fontWeight = FontWeight.Bold
        )
    }
}
