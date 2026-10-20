package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

const val BOARD_SIZE = 8

enum class GameMode {
    CLASSIC,
    ADVENTURE,
    DAILY,
    TIMED
}

enum class Difficulty(val level: Int, val minScore: Int, val stars: Int) {
    EASY(1, 0, 1),
    MEDIUM(2, 600, 2),
    HARD(3, 1600, 3),
    EXPERT(4, 3200, 4),
    MASTER(5, 5500, 5);

    companion object {
        fun fromScore(score: Int): Difficulty = when {
            score >= 5500 -> MASTER
            score >= 3200 -> EXPERT
            score >= 1600 -> HARD
            score >= 600 -> MEDIUM
            else -> EASY
        }
    }
}

enum class ThemeType(val displayName: String, val bg: Color, val gridBg: Color, val cellEmpty: Color, val border: Color) {
    NEON("Neón Glow", NeonBg, NeonGridBg, NeonGridCell, NeonGridCellBorder),
    WOOD("Madera Zen", WoodBg, WoodGridBg, WoodGridCell, WoodGridCellBorder),
    CANDY("Caramelo Pop", CandyBg, CandyGridBg, CandyGridCell, CandyGridCellBorder),
    GALAXY("Galaxia", Color(0xFF070B19), Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
}

enum class PowerUpType(val title: String, val cost: Int, val description: String) {
    ROTATE("Girar", 15, "Gira una pieza seleccionada"),
    HAMMER("Martillo", 25, "Rompe un bloque del tablero"),
    BOMB("Bomba 3x3", 40, "Explota un área de 3x3"),
    REFRESH("Cambiar", 10, "Renueva las 3 piezas actuales")
}

data class BoardCell(
    val row: Int,
    val col: Int,
    val color: Color? = null,
    val isObstacle: Boolean = false,
    val isGem: Boolean = false,
    val isClearing: Boolean = false,
    val placedTime: Long = 0L
) {
    val isEmpty: Boolean get() = color == null
}

data class Piece(
    val id: String = java.util.UUID.randomUUID().toString(),
    val coordinates: List<Pair<Int, Int>>, // relative (row, col) from top-left
    val color: Color,
    val canPlaceOnBoard: Boolean = true
) {
    val rows: Int get() = (coordinates.maxOfOrNull { it.first } ?: 0) + 1
    val cols: Int get() = (coordinates.maxOfOrNull { it.second } ?: 0) + 1
    val blockCount: Int get() = coordinates.size

    fun rotatedClockwise(): Piece {
        // Rotate 90 deg clockwise: (r, c) -> (c, rows - 1 - r)
        val maxR = rows - 1
        val newCoords = coordinates.map { (r, c) -> Pair(c, maxR - r) }
        val minR = newCoords.minOf { it.first }
        val minC = newCoords.minOf { it.second }
        val normalized = newCoords.map { (r, c) -> Pair(r - minR, c - minC) }
        return copy(id = java.util.UUID.randomUUID().toString(), coordinates = normalized)
    }
}

object PieceLibrary {
    private val blockColors = listOf(
        BlockCyan, BlockMagenta, BlockYellow, BlockGreen, BlockOrange, BlockPurple, BlockRed, BlockBlue
    )

    // Friendly, compact shapes that easily slot into small spaces
    private val easyShapes = listOf(
        // 1x1 single dot
        listOf(0 to 0),
        // 1x2 and 2x1 dominoes
        listOf(0 to 0, 0 to 1),
        listOf(0 to 0, 1 to 0),
        // 1x3 and 3x1 mini bars
        listOf(0 to 0, 0 to 1, 0 to 2),
        listOf(0 to 0, 1 to 0, 2 to 0),
        // 2x2 square
        listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1),
        // 2x2 corner (3 blocks) in 4 rotations
        listOf(0 to 0, 1 to 0, 1 to 1),
        listOf(0 to 0, 0 to 1, 1 to 0),
        listOf(0 to 1, 1 to 0, 1 to 1),
        listOf(0 to 0, 0 to 1, 1 to 1)
    )

    // Standard medium puzzle shapes
    private val mediumShapes = listOf(
        // 1x4 and 4x1 tetromino lines
        listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3),
        listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0),
        // 3x3 L-shapes (5 blocks) in 4 rotations
        listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 2 to 2),
        listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 2 to 0),
        listOf(0 to 2, 1 to 2, 2 to 0, 2 to 1, 2 to 2),
        listOf(0 to 0, 0 to 1, 0 to 2, 1 to 2, 2 to 2),
        // T-shapes
        listOf(0 to 0, 0 to 1, 0 to 2, 1 to 1),
        listOf(1 to 0, 1 to 1, 1 to 2, 0 to 1),
        listOf(0 to 0, 1 to 0, 2 to 0, 1 to 1),
        listOf(0 to 1, 1 to 1, 2 to 1, 1 to 0),
        // S and Z shapes
        listOf(0 to 1, 0 to 2, 1 to 0, 1 to 1),
        listOf(0 to 0, 0 to 1, 1 to 1, 1 to 2),
        listOf(0 to 0, 1 to 0, 1 to 1, 2 to 1),
        listOf(0 to 1, 1 to 1, 1 to 0, 2 to 0)
    )

    // Challenging, bulky and long pieces that require foresight
    private val hardShapes = listOf(
        // 1x5 and 5x1 long strips (very tricky to place when board fills up)
        listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4),
        listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0),
        // 3x3 full square (9 blocks massive space consumer!)
        listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 1 to 1, 1 to 2, 2 to 0, 2 to 1, 2 to 2),
        // Plus / Cross shape (5 blocks)
        listOf(0 to 1, 1 to 0, 1 to 1, 1 to 2, 2 to 1),
        // 2x3 and 3x2 rectangular blocks (6 blocks)
        listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 1 to 1, 1 to 2),
        listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1, 2 to 0, 2 to 1),
        // U-shape / Horseshoe (5 blocks)
        listOf(0 to 0, 1 to 0, 1 to 1, 1 to 2, 0 to 2)
    )

    fun getRandomPieces(count: Int = 3, difficulty: Difficulty = Difficulty.MEDIUM): List<Piece> {
        // Probability weights for [easy, medium, hard] pieces based on difficulty
        val (easyWeight, medWeight, hardWeight) = when (difficulty) {
            Difficulty.EASY -> Triple(65, 25, 10)
            Difficulty.MEDIUM -> Triple(35, 45, 20)
            Difficulty.HARD -> Triple(20, 45, 35)
            Difficulty.EXPERT -> Triple(10, 45, 45)
            Difficulty.MASTER -> Triple(5, 40, 55)
        }

        return (1..count).map {
            val roll = kotlin.random.Random.nextInt(100)
            val shapeList = when {
                roll < easyWeight -> easyShapes
                roll < easyWeight + medWeight -> mediumShapes
                else -> hardShapes
            }
            val shape = shapeList.random()
            val color = blockColors.random()
            Piece(coordinates = shape, color = color)
        }
    }
}

data class AdventureLevel(
    val levelNumber: Int,
    val title: String,
    val targetScore: Int,
    val targetLines: Int,
    val targetCombo: Int = 1,
    val movesLimit: Int? = null,
    val timeLimitSeconds: Int? = null,
    val initialObstacles: List<Pair<Int, Int>> = emptyList(),
    val rewardCoins: Int = 25
)

enum class ParticleShape {
    CIRCLE,
    STAR,
    DIAMOND,
    SHARD,
    SPARK
}

data class ClearParticle(
    val id: Long = System.nanoTime(),
    val startX: Float,
    val startY: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val shape: ParticleShape = ParticleShape.CIRCLE,
    val rotation: Float = 0f,
    val rotationSpeed: Float = 0f
)

data class ClearedCellInfo(
    val row: Int,
    val col: Int,
    val color: Color
)

data class LineClearEffect(
    val id: Long = System.currentTimeMillis(),
    val rows: List<Int> = emptyList(),
    val cols: List<Int> = emptyList(),
    val particles: List<ClearParticle> = emptyList(),
    val clearedCells: List<ClearedCellInfo> = emptyList()
)

data class ComboNotification(
    val text: String,
    val comboCount: Int,
    val bonusPoints: Int,
    val multiplier: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)
