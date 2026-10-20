package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.GameMode
import com.example.ui.components.*
import com.example.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    currentLanguage: com.example.localization.AppLanguage = com.example.localization.LocalCurrentLanguage.current,
    onSelectLanguage: (com.example.localization.AppLanguage) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // Handle physical / gesture back button
    BackHandler {
        if (!state.isGameOver && !state.isLevelWon) {
            viewModel.togglePause()
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("game_screen"),
        containerColor = state.theme.bg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            GameHeader(
                score = state.score,
                highScore = state.highScore,
                currentMultiplier = state.currentMultiplier,
                comboStreak = state.comboStreak,
                coins = state.coins,
                movesRemaining = state.movesRemaining,
                targetScore = state.currentLevel?.targetScore,
                gameMode = state.gameMode,
                isSoundEnabled = state.isSoundEnabled,
                onSoundToggle = { viewModel.toggleSound() },
                onPauseClick = { viewModel.togglePause() },
                timeRemainingSeconds = state.timeRemainingSeconds,
                initialTimeLimitSeconds = state.initialTimeLimitSeconds,
                isTimeCritical = state.isTimeCritical,
                recentTimeBonusSeconds = state.recentTimeBonusSeconds,
                difficulty = state.difficulty
            )

            // Game Board
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                val selectedPiece = state.selectedPieceIndex?.let { state.pieces.getOrNull(it) }
                GameBoard(
                    board = state.board,
                    theme = state.theme,
                    selectedPiece = selectedPiece,
                    activePowerUp = state.activePowerUp,
                    comboNotification = state.comboNotification,
                    clearEffect = state.clearEffect,
                    particles = state.particles,
                    onCellClick = { r, c -> viewModel.onCellClicked(r, c) }
                )
            }

            // Power-ups Bar
            PowerUpBar(
                coins = state.coins,
                activePowerUp = state.activePowerUp,
                onPowerUpClick = { powerUp -> viewModel.togglePowerUp(powerUp) }
            )

            // Piece Selector
            PieceSelector(
                pieces = state.pieces,
                selectedPieceIndex = state.selectedPieceIndex,
                theme = state.theme,
                onSelectPiece = { index -> viewModel.selectPiece(index) },
                onPiecePlacedAtAnchor = { index, piece, r, c ->
                    viewModel.placePiece(index, piece, r, c)
                }
            )

            // Banner automático debajo (AdMob Unit 1: ca-app-pub-1495262574338316/2803163788)
            com.example.ads.AdMobBanner(
                adUnitId = com.example.ads.AdMobConfig.BANNER_GAME_BOTTOM,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        val activity = androidx.compose.ui.platform.LocalContext.current as? android.app.Activity

        // Anuncios emergentes automáticos
        LaunchedEffect(state.isGameOver) {
            if (state.isGameOver && activity != null) {
                com.example.ads.AdMobManager(activity).showGameOverInterstitial(activity) {}
            }
        }

        LaunchedEffect(state.isLevelWon) {
            if (state.isLevelWon && activity != null) {
                com.example.ads.AdMobManager(activity).showLevelWinInterstitial(activity) {}
            }
        }

        // Dialogs
        if (state.isGameOver) {
            GameOverDialog(
                score = state.score,
                highScore = state.highScore,
                linesCleared = state.linesClearedThisGame,
                highestCombo = state.highestComboThisGame,
                isTimeOut = state.gameOverReasonIsTime,
                onPlayAgain = {
                    when (state.gameMode) {
                        com.example.model.GameMode.CLASSIC -> viewModel.startClassicGame()
                        com.example.model.GameMode.TIMED -> viewModel.startTimedGame()
                        com.example.model.GameMode.ADVENTURE -> state.currentLevel?.levelNumber?.let { viewModel.startAdventureLevel(it) }
                        com.example.model.GameMode.DAILY -> viewModel.startDailyChallenge()
                    }
                },
                onMainMenu = onNavigateBack
            )
        }

        if (state.isLevelWon) {
            LevelCompleteDialog(
                levelNumber = state.currentLevel?.levelNumber ?: 1,
                score = state.score,
                rewardCoins = state.currentLevel?.rewardCoins ?: 25,
                onNextLevel = {
                    val nextLvl = (state.currentLevel?.levelNumber ?: 1) + 1
                    viewModel.startAdventureLevel(nextLvl)
                },
                onMainMenu = onNavigateBack
            )
        }

        if (state.isPaused) {
            PauseDialog(
                onResume = { viewModel.togglePause() },
                onRestart = {
                    viewModel.togglePause()
                    viewModel.restartCurrentGame()
                },
                onMainMenu = {
                    viewModel.togglePause()
                    onNavigateBack()
                },
                isSoundEnabled = state.isSoundEnabled,
                isVibrationEnabled = state.isVibrationEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onToggleVibration = { viewModel.toggleVibration() },
                currentLanguage = currentLanguage,
                onSelectLanguage = onSelectLanguage
            )
        }
    }
}
