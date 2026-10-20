package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.GameSoundManager
import com.example.model.*
import com.example.repository.GamePreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameUiState(
    val gameMode: GameMode = GameMode.CLASSIC,
    val currentLevel: AdventureLevel? = null,
    val board: List<List<BoardCell>> = emptyList(),
    val pieces: List<Piece?> = listOf(null, null, null),
    val selectedPieceIndex: Int? = null,
    val hoveredAnchor: Pair<Int, Int>? = null,
    val score: Int = 0,
    val highScore: Int = 0,
    val currentMultiplier: Int = 1,
    val highestMultiplierThisGame: Int = 1,
    val comboStreak: Int = 0,
    val highestComboThisGame: Int = 0,
    val linesClearedThisGame: Int = 0,
    val movesRemaining: Int? = null,
    val timeRemainingSeconds: Int? = null,
    val initialTimeLimitSeconds: Int? = null,
    val isTimeCritical: Boolean = false,
    val recentTimeBonusSeconds: Int? = null,
    val gameOverReasonIsTime: Boolean = false,
    val difficulty: Difficulty = Difficulty.EASY,
    val coins: Int = 0,
    val isGameOver: Boolean = false,
    val isLevelWon: Boolean = false,
    val isPaused: Boolean = false,
    val activePowerUp: PowerUpType? = null,
    val theme: ThemeType = ThemeType.NEON,
    val comboNotification: ComboNotification? = null,
    val clearEffect: LineClearEffect? = null,
    val particles: List<ClearParticle> = emptyList(),
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val prefs = GamePreferences(application)
    val soundManager = GameSoundManager(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    init {
        soundManager.isSoundEnabled = prefs.isSoundEnabled
        soundManager.isVibrationEnabled = prefs.isVibrationEnabled
        _uiState.update {
            it.copy(
                theme = prefs.currentTheme,
                coins = prefs.coins,
                highScore = prefs.highScoreClassic,
                difficulty = prefs.selectedDifficulty,
                isSoundEnabled = prefs.isSoundEnabled,
                isVibrationEnabled = prefs.isVibrationEnabled
            )
        }
    }

    fun startClassicGame() {
        initGame(GameMode.CLASSIC, null)
    }

    fun startTimedGame(initialSeconds: Int = 60) {
        initGame(GameMode.TIMED, null, initialTimeSeconds = initialSeconds)
    }

    fun startAdventureLevel(levelNumber: Int) {
        val level = AdventureLevels.levels.firstOrNull { it.levelNumber == levelNumber }
            ?: AdventureLevels.levels.first()
        initGame(GameMode.ADVENTURE, level)
    }

    fun startDailyChallenge() {
        val level = AdventureLevels.getDailyLevel()
        initGame(GameMode.DAILY, level)
    }

    private fun initGame(mode: GameMode, level: AdventureLevel?, initialTimeSeconds: Int? = null) {
        timerJob?.cancel()

        val emptyBoard = List(BOARD_SIZE) { r ->
            List(BOARD_SIZE) { c ->
                val isObstacle = level?.initialObstacles?.contains(r to c) == true
                BoardCell(
                    row = r,
                    col = c,
                    color = if (isObstacle) Color(0xFF64748B) else null,
                    isObstacle = isObstacle
                )
            }
        }

        val startingDifficulty = if (mode == GameMode.ADVENTURE) {
            when {
                (level?.levelNumber ?: 1) >= 10 -> Difficulty.EXPERT
                (level?.levelNumber ?: 1) >= 6 -> Difficulty.HARD
                (level?.levelNumber ?: 1) >= 3 -> Difficulty.MEDIUM
                else -> Difficulty.EASY
            }
        } else {
            prefs.selectedDifficulty
        }

        val initialPieces = PieceLibrary.getRandomPieces(3, startingDifficulty)
        val updatedPieces = updatePiecePlacements(emptyBoard, initialPieces)

        val timeLimit = if (mode == GameMode.TIMED) {
            initialTimeSeconds ?: 60
        } else {
            level?.timeLimitSeconds
        }

        _uiState.update {
            it.copy(
                gameMode = mode,
                currentLevel = level,
                board = emptyBoard,
                pieces = updatedPieces,
                selectedPieceIndex = null,
                hoveredAnchor = null,
                score = 0,
                highScore = when (mode) {
                    GameMode.CLASSIC -> prefs.highScoreClassic
                    GameMode.TIMED -> prefs.highScoreTimed
                    else -> level?.targetScore ?: 0
                },
                currentMultiplier = 1,
                highestMultiplierThisGame = 1,
                comboStreak = 0,
                highestComboThisGame = 0,
                linesClearedThisGame = 0,
                movesRemaining = level?.movesLimit,
                timeRemainingSeconds = timeLimit,
                initialTimeLimitSeconds = timeLimit,
                isTimeCritical = false,
                recentTimeBonusSeconds = null,
                gameOverReasonIsTime = false,
                difficulty = startingDifficulty,
                isGameOver = false,
                isLevelWon = false,
                isPaused = false,
                activePowerUp = null,
                comboNotification = null,
                clearEffect = null,
                particles = emptyList()
            )
        }

        if (timeLimit != null) {
            startTimerCountdown()
        }
    }

    private fun startTimerCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val state = _uiState.value
                if (state.isPaused || state.isGameOver || state.isLevelWon) {
                    continue
                }
                val remaining = state.timeRemainingSeconds ?: break
                if (remaining > 1) {
                    val newRemaining = remaining - 1
                    if (newRemaining <= 10) {
                        soundManager.playTickWarning()
                    }
                    _uiState.update {
                        it.copy(
                            timeRemainingSeconds = newRemaining,
                            isTimeCritical = newRemaining <= 15
                        )
                    }
                } else {
                    // Time expired! Game over
                    _uiState.update {
                        it.copy(
                            timeRemainingSeconds = 0,
                            isTimeCritical = true,
                            isGameOver = true,
                            gameOverReasonIsTime = true
                        )
                    }
                    soundManager.playGameOver()
                    prefs.totalGamesPlayed += 1
                    break
                }
            }
        }
    }

    fun selectPiece(index: Int) {
        if (_uiState.value.isGameOver || _uiState.value.isLevelWon || _uiState.value.isPaused) return
        val currentSelected = _uiState.value.selectedPieceIndex
        val piece = _uiState.value.pieces.getOrNull(index)

        if (piece == null) return

        if (currentSelected == index) {
            _uiState.update { it.copy(selectedPieceIndex = null, hoveredAnchor = null) }
        } else {
            _uiState.update {
                it.copy(
                    selectedPieceIndex = index,
                    activePowerUp = null,
                    hoveredAnchor = null
                )
            }
            soundManager.playPlace()
        }
    }

    fun setHoverAnchor(anchor: Pair<Int, Int>?) {
        _uiState.update { it.copy(hoveredAnchor = anchor) }
    }

    fun onCellClicked(row: Int, col: Int) {
        val state = _uiState.value
        if (state.isGameOver || state.isLevelWon || state.isPaused) return

        // Power-up mode: Hammer
        if (state.activePowerUp == PowerUpType.HAMMER) {
            applyHammer(row, col)
            return
        }

        // Power-up mode: Bomb
        if (state.activePowerUp == PowerUpType.BOMB) {
            applyBomb(row, col)
            return
        }

        // Normal placement if a piece is selected
        val selectedIndex = state.selectedPieceIndex ?: return
        val piece = state.pieces.getOrNull(selectedIndex) ?: return

        placePiece(selectedIndex, piece, row, col)
    }

    fun placePiece(pieceIndex: Int, piece: Piece, anchorRow: Int, anchorCol: Int): Boolean {
        val state = _uiState.value
        if (state.isGameOver || state.isLevelWon || state.isPaused) {
            return false
        }
        if (!canPlace(state.board, piece, anchorRow, anchorCol)) {
            return false
        }

        // 1. Place blocks on board with timestamp for smooth glide entrance animation
        val placeTime = System.currentTimeMillis()
        val newBoard = state.board.map { rList ->
            rList.map { cell ->
                val matchingOffset = piece.coordinates.firstOrNull { (r, c) ->
                    anchorRow + r == cell.row && anchorCol + c == cell.col
                }
                if (matchingOffset != null) {
                    cell.copy(color = piece.color, isObstacle = false, placedTime = placeTime)
                } else {
                    cell
                }
            }
        }

        soundManager.playPlace()

        // 2. Add base placement points
        val placementPoints = piece.blockCount * 10
        var currentScore = state.score + placementPoints

        // 3. Consume placed piece
        val updatedPieces = state.pieces.toMutableList()
        updatedPieces[pieceIndex] = null

        // 4. Check for full rows and columns
        val fullRows = (0 until BOARD_SIZE).filter { r ->
            newBoard[r].all { !it.isEmpty }
        }
        val fullCols = (0 until BOARD_SIZE).filter { c ->
            (0 until BOARD_SIZE).all { r -> !newBoard[r][c].isEmpty }
        }

        val totalClearedLines = fullRows.size + fullCols.size
        var newCombo = state.comboStreak
        var newHighestCombo = state.highestComboThisGame
        var newTotalLines = state.linesClearedThisGame + totalClearedLines
        var boardAfterClear = newBoard
        var clearingBoard = newBoard
        var newMultiplier = 1
        var newHighestMultiplier = state.highestMultiplierThisGame

        if (totalClearedLines > 0) {
            newCombo += 1
            if (newCombo > newHighestCombo) newHighestCombo = newCombo

            // Multiplier increases when multiple rows/columns are cleared simultaneously:
            // 1 line = 1x base
            // 2 lines simultaneously = 2x multiplier
            // 3 lines simultaneously = 3x multiplier
            // 4 lines simultaneously = 5x multiplier
            // 5+ lines simultaneously = 8x multiplier
            val simultaneousMultiplier = when (totalClearedLines) {
                1 -> 1
                2 -> 2
                3 -> 3
                4 -> 5
                else -> 8
            }

            // Stack simultaneous multiplier with consecutive combo streak
            newMultiplier = simultaneousMultiplier + (newCombo - 1)
            if (newMultiplier > newHighestMultiplier) newHighestMultiplier = newMultiplier
            if (newMultiplier > prefs.highestMultiplierRecord) {
                prefs.highestMultiplierRecord = newMultiplier
            }

            // Calculate combo score multiplier
            val linePoints = 100 * totalClearedLines * newMultiplier
            currentScore += linePoints

            // First mark cells as isClearing = true so Compose displays the dissolving / flash effect
            clearingBoard = newBoard.map { rList ->
                rList.map { cell ->
                    val inFullRow = fullRows.contains(cell.row)
                    val inFullCol = fullCols.contains(cell.col)
                    if (inFullRow || inFullCol) {
                        cell.copy(isClearing = true)
                    } else {
                        cell
                    }
                }
            }

            // Clear cells from board after flash
            boardAfterClear = newBoard.map { rList ->
                rList.map { cell ->
                    val inFullRow = fullRows.contains(cell.row)
                    val inFullCol = fullCols.contains(cell.col)
                    if (inFullRow || inFullCol) {
                        cell.copy(color = null, isObstacle = false, isClearing = false)
                    } else {
                        cell
                    }
                }
            }

            // Trigger sound & haptics with distinct feedback for line clears and multiplier increases
            soundManager.playLineClear(totalClearedLines, newMultiplier)
            if (newMultiplier > 1) {
                soundManager.playMultiplierIncrease(newMultiplier)
            }

            // Trigger combo message
            val comboMsg = when {
                totalClearedLines >= 4 -> "¡QUAD! x$newMultiplier MULTIPLICADOR 🔥🔥"
                totalClearedLines == 3 -> "¡TRIPLE! x$newMultiplier MULTIPLICADOR 🔥"
                totalClearedLines == 2 -> "¡DOBLE! x$newMultiplier MULTIPLICADOR"
                newMultiplier >= 5 -> "¡LEGENDARIO! x$newMultiplier MULTI 🔥🔥🔥"
                newMultiplier >= 3 -> "¡SUPER COMBO! x$newMultiplier MULTI 🔥"
                else -> "¡LÍNEA! +$linePoints"
            }

            showComboBanner(comboMsg, newCombo, linePoints, newMultiplier)

            // Spawn particle effects and smooth clearing animation
            spawnClearParticles(fullRows, fullCols, piece.color, newBoard)

            // Earn coins
            val earnedCoins = totalClearedLines * 2 + (if (newMultiplier > 1) newMultiplier * 2 else 0)
            val newCoinBalance = state.coins + earnedCoins
            prefs.coins = newCoinBalance

            // Update stats
            prefs.totalLinesCleared += totalClearedLines
            if (newCombo > prefs.highestComboStreak) {
                prefs.highestComboStreak = newCombo
            }
        } else {
            // Reset combo streak and multiplier if no line was cleared
            newCombo = 0
            newMultiplier = 1
        }

        // Calculate dynamic difficulty progression based on current score
        val currentDifficulty = if (state.gameMode == GameMode.ADVENTURE) {
            state.difficulty
        } else {
            val scoreDiff = Difficulty.fromScore(currentScore)
            if (scoreDiff.level > prefs.selectedDifficulty.level) scoreDiff else prefs.selectedDifficulty
        }

        if (currentDifficulty.level > state.difficulty.level) {
            val strings = com.example.localization.AppStrings.get(prefs.appLanguage)
            val diffName = when (currentDifficulty) {
                Difficulty.EASY -> strings.diffEasy
                Difficulty.MEDIUM -> strings.diffMedium
                Difficulty.HARD -> strings.diffHard
                Difficulty.EXPERT -> strings.diffExpert
                Difficulty.MASTER -> strings.diffMaster
            }
            showComboBanner("${strings.difficultyIncreased} $diffName! 🔥", currentDifficulty.level, 0, 1)
            soundManager.playMultiplierIncrease(currentDifficulty.level)
        }

        // Award time bonus in timed modes when lines are cleared
        var updatedTimeRemaining = state.timeRemainingSeconds
        var awardedTimeBonus: Int? = null
        if (totalClearedLines > 0 && updatedTimeRemaining != null) {
            val bonusSec = totalClearedLines * 3 + (newMultiplier - 1) * 2
            updatedTimeRemaining = (updatedTimeRemaining + bonusSec).coerceAtMost(180)
            awardedTimeBonus = bonusSec
            soundManager.playTimeBonus()

            viewModelScope.launch {
                delay(1500)
                _uiState.update {
                    if (it.recentTimeBonusSeconds == bonusSec) it.copy(recentTimeBonusSeconds = null) else it
                }
            }
        }

        // Check if all pieces were placed -> replenish 3 new ones scaled to current difficulty
        val finalPieces = if (updatedPieces.all { it == null }) {
            PieceLibrary.getRandomPieces(3, currentDifficulty)
        } else {
            updatedPieces
        }

        // Validate pieces feasibility on the new board
        val validatedPieces = updatePiecePlacements(boardAfterClear, finalPieces)

        // Moves remaining for adventure mode
        val newMoves = state.movesRemaining?.let { it - 1 }

        // Update High Score
        if (state.gameMode == GameMode.CLASSIC && currentScore > state.highScore) {
            prefs.highScoreClassic = currentScore
        } else if (state.gameMode == GameMode.TIMED && currentScore > state.highScore) {
            prefs.highScoreTimed = currentScore
        }

        // Check win / lose condition
        val isWon = checkWinCondition(state.gameMode, state.currentLevel, currentScore, newTotalLines, newHighestCombo)
        val isMovesOut = newMoves != null && newMoves <= 0
        val cannotPlaceAny = validatedPieces.filterNotNull().none { it.canPlaceOnBoard }
        val isOver = !isWon && (isMovesOut || cannotPlaceAny)

        if (isWon) {
            soundManager.playLevelWin()
            state.currentLevel?.let { lvl ->
                val stars = calculateStars(currentScore, lvl.targetScore)
                prefs.setLevelStars(lvl.levelNumber, stars)
                if (lvl.levelNumber >= prefs.highestUnlockedLevel && prefs.highestUnlockedLevel < 12) {
                    prefs.highestUnlockedLevel = lvl.levelNumber + 1
                }
                prefs.coins += lvl.rewardCoins
            }
        } else if (isOver) {
            soundManager.playGameOver()
            prefs.totalGamesPlayed += 1
        }

        val initialDisplayBoard = if (totalClearedLines > 0) clearingBoard else newBoard

        _uiState.update {
            it.copy(
                board = initialDisplayBoard,
                pieces = validatedPieces,
                selectedPieceIndex = null,
                hoveredAnchor = null,
                score = currentScore,
                highScore = when (state.gameMode) {
                    GameMode.CLASSIC -> maxOf(currentScore, state.highScore)
                    GameMode.TIMED -> maxOf(currentScore, state.highScore)
                    else -> state.highScore
                },
                currentMultiplier = newMultiplier,
                highestMultiplierThisGame = newHighestMultiplier,
                comboStreak = newCombo,
                highestComboThisGame = newHighestCombo,
                linesClearedThisGame = newTotalLines,
                movesRemaining = newMoves,
                timeRemainingSeconds = updatedTimeRemaining,
                recentTimeBonusSeconds = awardedTimeBonus ?: it.recentTimeBonusSeconds,
                difficulty = currentDifficulty,
                coins = prefs.coins,
                isGameOver = isOver,
                isLevelWon = isWon
            )
        }

        if (totalClearedLines > 0) {
            viewModelScope.launch {
                delay(300)
                _uiState.update {
                    it.copy(board = boardAfterClear)
                }
            }
        }

        return true
    }

    private fun checkWinCondition(
        mode: GameMode,
        level: AdventureLevel?,
        score: Int,
        lines: Int,
        highestCombo: Int
    ): Boolean {
        if (mode == GameMode.CLASSIC || level == null) return false
        val scoreMet = score >= level.targetScore
        val linesMet = lines >= level.targetLines
        val comboMet = highestCombo >= level.targetCombo
        return scoreMet && linesMet && comboMet
    }

    private fun calculateStars(score: Int, target: Int): Int {
        val ratio = score.toFloat() / target.coerceAtLeast(1)
        return when {
            ratio >= 1.4f -> 3
            ratio >= 1.15f -> 2
            else -> 1
        }
    }

    private fun canPlace(board: List<List<BoardCell>>, piece: Piece, anchorRow: Int, anchorCol: Int): Boolean {
        for ((r, c) in piece.coordinates) {
            val targetR = anchorRow + r
            val targetC = anchorCol + c
            if (targetR !in 0 until BOARD_SIZE || targetC !in 0 until BOARD_SIZE) {
                return false
            }
            if (!board[targetR][targetC].isEmpty) {
                return false
            }
        }
        return true
    }

    private fun updatePiecePlacements(board: List<List<BoardCell>>, pieces: List<Piece?>): List<Piece?> {
        return pieces.map { piece ->
            if (piece == null) null
            else {
                val canFit = canPieceFitAnywhere(board, piece)
                piece.copy(canPlaceOnBoard = canFit)
            }
        }
    }

    private fun canPieceFitAnywhere(board: List<List<BoardCell>>, piece: Piece): Boolean {
        for (r in 0 until BOARD_SIZE) {
            for (c in 0 until BOARD_SIZE) {
                if (canPlace(board, piece, r, c)) {
                    return true
                }
            }
        }
        return false
    }

    // Power-up implementations
    fun togglePowerUp(type: PowerUpType) {
        val state = _uiState.value
        if (state.isGameOver || state.isLevelWon || state.isPaused) return
        if (state.coins < type.cost) return

        if (type == PowerUpType.ROTATE) {
            rotateSelectedPiece()
            return
        }
        if (type == PowerUpType.REFRESH) {
            refreshPieces()
            return
        }

        // Hammer or Bomb toggle
        if (state.activePowerUp == type) {
            _uiState.update { it.copy(activePowerUp = null) }
        } else {
            _uiState.update { it.copy(activePowerUp = type, selectedPieceIndex = null) }
            soundManager.playPowerUp()
        }
    }

    private fun rotateSelectedPiece() {
        val state = _uiState.value
        val selectedIndex = state.selectedPieceIndex ?: return
        val piece = state.pieces.getOrNull(selectedIndex) ?: return
        if (state.coins < PowerUpType.ROTATE.cost) return

        val rotated = piece.rotatedClockwise()
        val updatedPieces = state.pieces.toMutableList()
        updatedPieces[selectedIndex] = rotated

        val validated = updatePiecePlacements(state.board, updatedPieces)
        val newCoins = state.coins - PowerUpType.ROTATE.cost
        prefs.coins = newCoins

        soundManager.playPowerUp()
        _uiState.update {
            it.copy(
                pieces = validated,
                coins = newCoins
            )
        }
    }

    private fun refreshPieces() {
        val state = _uiState.value
        if (state.coins < PowerUpType.REFRESH.cost) return

        val freshPieces = PieceLibrary.getRandomPieces(3, state.difficulty)
        val validated = updatePiecePlacements(state.board, freshPieces)
        val newCoins = state.coins - PowerUpType.REFRESH.cost
        prefs.coins = newCoins

        soundManager.playPowerUp()
        _uiState.update {
            it.copy(
                pieces = validated,
                selectedPieceIndex = null,
                coins = newCoins
            )
        }
    }

    private fun applyHammer(row: Int, col: Int) {
        val state = _uiState.value
        if (state.coins < PowerUpType.HAMMER.cost) return
        val cell = state.board[row][col]
        if (cell.isEmpty) return

        val newBoard = state.board.map { rList ->
            rList.map { c ->
                if (c.row == row && c.col == col) c.copy(color = null, isObstacle = false)
                else c
            }
        }

        val newCoins = state.coins - PowerUpType.HAMMER.cost
        prefs.coins = newCoins
        soundManager.playPowerUp()

        val validatedPieces = updatePiecePlacements(newBoard, state.pieces)

        _uiState.update {
            it.copy(
                board = newBoard,
                pieces = validatedPieces,
                activePowerUp = null,
                coins = newCoins
            )
        }
    }

    private fun applyBomb(centerRow: Int, centerCol: Int) {
        val state = _uiState.value
        if (state.coins < PowerUpType.BOMB.cost) return

        val newBoard = state.board.map { rList ->
            rList.map { c ->
                val inRadius = kotlin.math.abs(c.row - centerRow) <= 1 && kotlin.math.abs(c.col - centerCol) <= 1
                if (inRadius) c.copy(color = null, isObstacle = false)
                else c
            }
        }

        val newCoins = state.coins - PowerUpType.BOMB.cost
        prefs.coins = newCoins
        soundManager.playComboCheer()

        val validatedPieces = updatePiecePlacements(newBoard, state.pieces)

        _uiState.update {
            it.copy(
                board = newBoard,
                pieces = validatedPieces,
                activePowerUp = null,
                coins = newCoins
            )
        }
    }

    private fun showComboBanner(text: String, count: Int, bonus: Int, multiplier: Int = 1) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(comboNotification = ComboNotification(text, count, bonus, multiplier))
            }
            delay(1500)
            _uiState.update {
                if (it.comboNotification?.text == text) {
                    it.copy(comboNotification = null)
                } else {
                    it
                }
            }
        }
    }

    private fun spawnClearParticles(rows: List<Int>, cols: List<Int>, color: Color, boardBeforeClear: List<List<BoardCell>> = emptyList()) {
        val newParticles = mutableListOf<ClearParticle>()
        val clearedCellsList = mutableListOf<ClearedCellInfo>()
        val accentGold = Color(0xFFFFD700)
        val brightWhite = Color.White
        val colors = listOf(color, accentGold, brightWhite, Color(0xFFFF2A85), Color(0xFF00E5FF), Color(0xFFFFD600))

        for (r in rows) {
            for (c in 0 until BOARD_SIZE) {
                val cellColor = boardBeforeClear.getOrNull(r)?.getOrNull(c)?.color ?: color
                clearedCellsList.add(ClearedCellInfo(r, c, cellColor))
                newParticles.addAll(generateCellParticles(r, c, colors))
            }
        }
        for (c in cols) {
            for (r in 0 until BOARD_SIZE) {
                // Avoid duplicating intersections if both row and col cleared
                if (!rows.contains(r)) {
                    val cellColor = boardBeforeClear.getOrNull(r)?.getOrNull(c)?.color ?: color
                    clearedCellsList.add(ClearedCellInfo(r, c, cellColor))
                    newParticles.addAll(generateCellParticles(r, c, colors))
                }
            }
        }

        val effect = LineClearEffect(
            id = System.currentTimeMillis(),
            rows = rows,
            cols = cols,
            particles = newParticles.take(120),
            clearedCells = clearedCellsList
        )

        _uiState.update { it.copy(clearEffect = effect, particles = effect.particles) }

        viewModelScope.launch {
            delay(750)
            _uiState.update {
                if (it.clearEffect?.id == effect.id) {
                    it.copy(clearEffect = null, particles = emptyList())
                } else it
            }
        }
    }

    private fun generateCellParticles(r: Int, c: Int, colors: List<Color>): List<ClearParticle> {
        val shapes = listOf(ParticleShape.STAR, ParticleShape.CIRCLE, ParticleShape.DIAMOND)
        val count = Random.nextInt(3, 5)
        return (0 until count).map {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 0.28f + 0.08f
            ClearParticle(
                startX = (c + 0.5f) / BOARD_SIZE,
                startY = (r + 0.5f) / BOARD_SIZE,
                vx = kotlin.math.cos(angle) * speed,
                vy = kotlin.math.sin(angle) * speed,
                color = colors.random(),
                size = Random.nextFloat() * 9f + 5f,
                shape = shapes.random()
            )
        }
    }

    fun setTheme(theme: ThemeType) {
        prefs.currentTheme = theme
        _uiState.update { it.copy(theme = theme) }
    }

    fun toggleSound() {
        val newSound = !_uiState.value.isSoundEnabled
        prefs.isSoundEnabled = newSound
        soundManager.isSoundEnabled = newSound
        _uiState.update { it.copy(isSoundEnabled = newSound) }
    }

    fun toggleVibration() {
        val newVib = !_uiState.value.isVibrationEnabled
        prefs.isVibrationEnabled = newVib
        soundManager.isVibrationEnabled = newVib
        _uiState.update { it.copy(isVibrationEnabled = newVib) }
    }

    fun togglePause() {
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun restartCurrentGame() {
        when (_uiState.value.gameMode) {
            GameMode.CLASSIC -> startClassicGame()
            GameMode.TIMED -> startTimedGame(_uiState.value.initialTimeLimitSeconds ?: 60)
            GameMode.ADVENTURE -> {
                _uiState.value.currentLevel?.let { startAdventureLevel(it.levelNumber) } ?: startClassicGame()
            }
            GameMode.DAILY -> startDailyChallenge()
        }
    }

    fun setDifficulty(difficulty: Difficulty) {
        prefs.selectedDifficulty = difficulty
        _uiState.update { it.copy(difficulty = difficulty) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        soundManager.release()
    }
}
