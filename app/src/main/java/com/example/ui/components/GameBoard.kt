package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun GameBoard(
    board: List<List<BoardCell>>,
    theme: ThemeType,
    selectedPiece: Piece?,
    activePowerUp: PowerUpType?,
    comboNotification: ComboNotification?,
    clearEffect: LineClearEffect?,
    particles: List<ClearParticle> = emptyList(),
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(clearEffect?.id) {
        if (clearEffect != null) {
            animProgress.snapTo(0f)
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = LinearOutSlowInEasing)
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(theme.gridBg)
            .border(2.5.dp, theme.border, RoundedCornerShape(20.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        val boardWidth = maxWidth
        val cellSize = (boardWidth - 14.dp) / BOARD_SIZE

        // Render 8x8 Grid
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("game_board_grid"),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (r in 0 until BOARD_SIZE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (c in 0 until BOARD_SIZE) {
                        val cell = board[r][c]
                        BoardCellView(
                            cell = cell,
                            theme = theme,
                            size = cellSize,
                            isHighlighted = isCellHighlighted(r, c, selectedPiece, activePowerUp),
                            onClick = { onCellClick(r, c) }
                        )
                    }
                }
            }
        }

        // Particle Explosions & Flash Beams Overlay
        val activeParticles = clearEffect?.particles ?: particles
        val progress = animProgress.value
        if (clearEffect != null && progress < 1f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height
                val cellW = canvasW / BOARD_SIZE
                val cellH = canvasH / BOARD_SIZE

                // 1. Smooth Cell Shrink & Flash Animation for Completed Lines
                val p = progress
                for (cleared in clearEffect.clearedCells) {
                    val cLeft = cleared.col * cellW
                    val cTop = cleared.row * cellH
                    val cx = cLeft + cellW / 2f
                    val cy = cTop + cellH / 2f

                    if (p < 0.8f) {
                        val (scale, alpha, flashWhite) = if (p <= 0.22f) {
                            val norm = p / 0.22f
                            val s = 1.0f + norm * 0.16f
                            val a = 1.0f
                            val f = kotlin.math.sin(norm * Math.PI.toFloat()) * 0.85f
                            Triple(s, a, f)
                        } else {
                            val norm = (p - 0.22f) / 0.58f
                            val s = (1.16f * (1.0f - norm)).coerceAtLeast(0f)
                            val a = (1.0f - norm * norm).coerceIn(0f, 1f)
                            val f = 0f
                            Triple(s, a, f)
                        }

                        val drawW = (cellW - 3f) * scale
                        val drawH = (cellH - 3f) * scale
                        val corner = CornerRadius(12f + (1f - scale) * 12f)

                        if (drawW > 0f && drawH > 0f) {
                            drawRoundRect(
                                color = cleared.color.copy(alpha = alpha),
                                topLeft = Offset(cx - drawW / 2f, cy - drawH / 2f),
                                size = Size(drawW, drawH),
                                cornerRadius = corner
                            )
                            if (flashWhite > 0f) {
                                drawRoundRect(
                                    color = Color.White.copy(alpha = (flashWhite * alpha).coerceIn(0f, 1f)),
                                    topLeft = Offset(cx - drawW / 2f, cy - drawH / 2f),
                                    size = Size(drawW, drawH),
                                    cornerRadius = corner
                                )
                            }
                        }
                    }
                }

                // 2. Line Clear Flash Beams: bright burst of light along completed lines
                val beamAlpha = ((1f - progress) * 0.85f).coerceIn(0f, 1f)
                for (r in clearEffect.rows) {
                    val top = r * cellH
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = beamAlpha * 0.2f),
                                Color.White.copy(alpha = beamAlpha),
                                Color.White.copy(alpha = beamAlpha * 0.2f)
                            ),
                            startY = top,
                            endY = top + cellH
                        ),
                        topLeft = Offset(0f, top),
                        size = Size(canvasW, cellH)
                    )
                }
                for (c in clearEffect.cols) {
                    val left = c * cellW
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = beamAlpha * 0.2f),
                                Color.White.copy(alpha = beamAlpha),
                                Color.White.copy(alpha = beamAlpha * 0.2f)
                            ),
                            startX = left,
                            endX = left + cellW
                        ),
                        topLeft = Offset(left, 0f),
                        size = Size(cellW, canvasH)
                    )
                }

                // 2. Animated Particle Explosion
                val gravity = 0.12f
                for (p in activeParticles) {
                    val px = (p.startX + p.vx * progress) * canvasW
                    val py = (p.startY + p.vy * progress + gravity * progress * progress) * canvasH
                    val alpha = ((1f - progress) * 1.35f).coerceIn(0f, 1f)
                    val currentSize = p.size * (1f - progress * 0.45f)

                    when (p.shape) {
                        ParticleShape.CIRCLE -> {
                            // Outer glow halo
                            drawCircle(
                                color = p.color.copy(alpha = alpha * 0.45f),
                                radius = currentSize * 1.6f,
                                center = Offset(px, py)
                            )
                            // Main particle
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = currentSize,
                                center = Offset(px, py)
                            )
                            // White sparkling core
                            drawCircle(
                                color = Color.White.copy(alpha = alpha * 0.9f),
                                radius = currentSize * 0.45f,
                                center = Offset(px, py)
                            )
                        }
                        ParticleShape.STAR -> {
                            // 4-point sparkle star
                            val starPath = Path().apply {
                                moveTo(px, py - currentSize * 1.9f)
                                lineTo(px + currentSize * 0.4f, py)
                                lineTo(px, py + currentSize * 1.9f)
                                lineTo(px - currentSize * 0.4f, py)
                                close()
                                moveTo(px - currentSize * 1.9f, py)
                                lineTo(px, py + currentSize * 0.4f)
                                lineTo(px + currentSize * 1.9f, py)
                                lineTo(px, py - currentSize * 0.4f)
                                close()
                            }
                            drawPath(starPath, color = p.color.copy(alpha = alpha))
                            drawCircle(
                                color = Color.White.copy(alpha = alpha),
                                radius = currentSize * 0.45f,
                                center = Offset(px, py)
                            )
                        }
                        ParticleShape.DIAMOND -> {
                            val diamondPath = Path().apply {
                                moveTo(px, py - currentSize * 1.3f)
                                lineTo(px + currentSize * 1.3f, py)
                                lineTo(px, py + currentSize * 1.3f)
                                lineTo(px - currentSize * 1.3f, py)
                                close()
                            }
                            drawPath(diamondPath, color = p.color.copy(alpha = alpha))
                        }
                        else -> {
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = currentSize,
                                center = Offset(px, py)
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = alpha * 0.8f),
                                radius = currentSize * 0.4f,
                                center = Offset(px, py)
                            )
                        }
                    }
                }
            }
        }

        // Floating Combo Cheer Banner
        AnimatedVisibility(
            visible = comboNotification != null,
            enter = scaleIn(spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            comboNotification?.let { notif ->
                ComboBannerView(notif = notif)
            }
        }
    }
}

private fun isCellHighlighted(
    r: Int,
    c: Int,
    selectedPiece: Piece?,
    activePowerUp: PowerUpType?
): Boolean {
    if (activePowerUp == PowerUpType.HAMMER || activePowerUp == PowerUpType.BOMB) {
        return true
    }
    return false
}

@Composable
fun BoardCellView(
    cell: BoardCell,
    theme: ThemeType,
    size: androidx.compose.ui.unit.Dp,
    isHighlighted: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .size(size)
            .padding(1.5.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (cell.isEmpty) theme.cellEmpty
                else cell.color ?: theme.cellEmpty
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
            .testTag("cell_${cell.row}_${cell.col}")
            .drawBehind {
                if (!cell.isEmpty && cell.color != null) {
                    // Glossy 3D top bevel
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.45f),
                                Color.Transparent
                            )
                        ),
                        size = Size(this.size.width, this.size.height * 0.4f),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                    // Bottom shadow border
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.35f),
                        size = this.size,
                        cornerRadius = CornerRadius(12f, 12f),
                        style = Stroke(width = 2.5f)
                    )
                } else {
                    // Empty cell subtle inset border
                    drawRoundRect(
                        color = theme.border.copy(alpha = 0.4f),
                        size = this.size,
                        cornerRadius = CornerRadius(10f, 10f),
                        style = Stroke(width = 1.2f)
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (cell.isObstacle) {
            // Ice / Obstacle pattern
            Box(
                modifier = Modifier
                    .fillMaxSize(0.7f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
fun ComboBannerView(notif: ComboNotification) {
    val infiniteTransition = rememberInfiniteTransition(label = "combo_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .shadow(24.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFF1744),
                        Color(0xFFFF6D00),
                        Color(0xFFFFD600)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Combo",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = notif.text,
                    fontFamily = RighteousFontFamily,
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+${notif.bonusPoints} PUNTOS",
                        fontFamily = FredokaFontFamily,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        fontWeight = FontWeight.Bold
                    )
                    if (notif.multiplier > 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(x${notif.multiplier})",
                            fontFamily = RighteousFontFamily,
                            fontSize = 15.sp,
                            color = AccentGold,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
