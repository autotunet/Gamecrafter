package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Piece
import com.example.model.ThemeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PieceSelector(
    pieces: List<Piece?>,
    selectedPieceIndex: Int?,
    theme: ThemeType,
    onSelectPiece: (Int) -> Unit,
    onPiecePlacedAtAnchor: (Int, Piece, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until 3) {
            val piece = pieces.getOrNull(index)
            val isSelected = selectedPieceIndex == index

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Subtle static placeholder slot
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.gridBg.copy(alpha = 0.4f))
                        .border(1.dp, theme.border.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                )

                // Staggered animated entrance slot
                AnimatedPieceSlot(
                    index = index,
                    piece = piece,
                    isSelected = isSelected,
                    theme = theme,
                    onSelectPiece = { onSelectPiece(index) }
                )
            }
        }
    }
}

@Composable
fun AnimatedPieceSlot(
    index: Int,
    piece: Piece?,
    isSelected: Boolean,
    theme: ThemeType,
    onSelectPiece: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entranceScale = remember { Animatable(0f) }
    val entranceAlpha = remember { Animatable(0f) }
    val entranceTranslationY = remember { Animatable(36f) }

    LaunchedEffect(piece) {
        if (piece != null) {
            entranceScale.snapTo(0.15f)
            entranceAlpha.snapTo(0f)
            entranceTranslationY.snapTo(36f)
            delay(index * 75L) // Staggered dealing animation
            launch {
                entranceAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 260, easing = LinearOutSlowInEasing)
                )
            }
            launch {
                entranceTranslationY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = 0.65f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
            entranceScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.58f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        } else {
            entranceScale.snapTo(0f)
            entranceAlpha.snapTo(0f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = entranceScale.value
                scaleY = entranceScale.value
                alpha = entranceAlpha.value
                translationY = entranceTranslationY.value
            },
        contentAlignment = Alignment.Center
    ) {
        if (piece != null) {
            PieceSlotCard(
                piece = piece,
                isSelected = isSelected,
                theme = theme,
                onClick = onSelectPiece
            )
        }
    }
}

@Composable
fun PieceSlotCard(
    piece: Piece,
    isSelected: Boolean,
    theme: ThemeType,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1f,
        label = "scale"
    )

    val alpha = if (piece.canPlaceOnBoard) 1f else 0.4f
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(scale)
            .alpha(alpha)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) theme.cellEmpty.copy(alpha = 0.9f)
                else theme.cellEmpty.copy(alpha = 0.45f)
            )
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) Color(0xFFFFD700) else theme.border.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .padding(8.dp)
            .testTag("piece_slot"),
        contentAlignment = Alignment.Center
    ) {
        PieceMiniView(piece = piece)
    }
}

@Composable
fun PieceMiniView(
    piece: Piece,
    modifier: Modifier = Modifier
) {
    val maxRows = piece.rows
    val maxCols = piece.cols
    val blockSize = 17.dp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (r in 0 until maxRows) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (c in 0 until maxCols) {
                    val hasBlock = piece.coordinates.contains(Pair(r, c))
                    if (hasBlock) {
                        Box(
                            modifier = Modifier
                                .size(blockSize)
                                .padding(1.dp)
                                .clip(RoundedCornerShape(3.5.dp))
                                .background(piece.color)
                                .drawBehind {
                                    // Glossy top
                                    drawRoundRect(
                                        brush = Brush.verticalGradient(
                                            listOf(Color.White.copy(alpha = 0.45f), Color.Transparent)
                                        ),
                                        size = Size(this.size.width, this.size.height * 0.45f),
                                        cornerRadius = CornerRadius(6f, 6f)
                                    )
                                    // Subtle outline
                                    drawRoundRect(
                                        color = Color.Black.copy(alpha = 0.25f),
                                        size = this.size,
                                        cornerRadius = CornerRadius(6f, 6f),
                                        style = Stroke(width = 1.5f)
                                    )
                                }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(blockSize))
                    }
                }
            }
        }
    }
}
